"""
Genera MP3 de todas las paradas con voz masculina (Edge TTS).
Uso: python tools/generate_all_audio.py
Requisitos: pip install edge-tts
"""
from __future__ import annotations

import asyncio
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "app" / "src" / "main" / "assets"
TOURS_JSON = ASSETS / "tours" / "edinburgh.json"
SCRIPTS_DIR = ASSETS / "scripts"

# Voz masculina española (neural, buena calidad)
VOICE = "es-ES-AlvaroNeural"

# Ritmo por tipo de tour
RATE_BY_TOUR = {
    "oldtown-historic": "-5%",
    "newtown-cheerful": "+5%",
    "oldtown-ghosts": "-10%",
    "arthur-holyrood": "+0%",
}

SCRIPT_FILE_BY_TOUR = {
    "oldtown-historic": "oldtown-historic.json",
    "newtown-cheerful": "newtown-cheerful.json",
    "oldtown-ghosts": "oldtown-ghosts.json",
    "arthur-holyrood": "arthur-holyrood.json",
}

MAX_CHARS = 4800  # límite seguro por petición TTS


def load_json(path: Path) -> dict:
    with path.open(encoding="utf-8") as f:
        return json.load(f)


def normalize_text(text: str) -> str:
    text = text.replace("\n\n", " ").replace("\n", " ")
    text = re.sub(r"\s+", " ", text).strip()
    return text


def chunk_text(text: str, max_len: int = MAX_CHARS) -> list[str]:
    if len(text) <= max_len:
        return [text]
    chunks: list[str] = []
    sentences = re.split(r"(?<=[.!?])\s+", text)
    current = ""
    for sentence in sentences:
        if len(current) + len(sentence) + 1 <= max_len:
            current = f"{current} {sentence}".strip()
        else:
            if current:
                chunks.append(current)
            current = sentence
    if current:
        chunks.append(current)
    return chunks


async def synthesize_to_mp3(text: str, output: Path, rate: str) -> None:
    import edge_tts

    output.parent.mkdir(parents=True, exist_ok=True)
    chunks = chunk_text(text)
    if len(chunks) == 1:
        communicate = edge_tts.Communicate(chunks[0], VOICE, rate=rate)
        await communicate.save(str(output))
        return

    # Varios fragmentos: guardar temporal y concatenar con pydub si existe, si no uno a uno el último gana — mejor usar lista y ffmpeg
    temp_files: list[Path] = []
    for i, chunk in enumerate(chunks):
        temp = output.with_suffix(f".part{i}.mp3")
        communicate = edge_tts.Communicate(chunk, VOICE, rate=rate)
        await communicate.save(str(temp))
        temp_files.append(temp)

    try:
        from pydub import AudioSegment

        combined = AudioSegment.empty()
        for temp in temp_files:
            combined += AudioSegment.from_mp3(temp)
        combined.export(str(output), format="mp3", bitrate="128k")
    except ImportError:
        # Sin pydub: unir con ffmpeg si está disponible
        import shutil
        import subprocess

        if shutil.which("ffmpeg"):
            list_file = output.with_suffix(".txt")
            list_file.write_text(
                "\n".join(f"file '{t.resolve().as_posix()}'" for t in temp_files),
                encoding="utf-8",
            )
            subprocess.run(
                [
                    "ffmpeg",
                    "-y",
                    "-f",
                    "concat",
                    "-safe",
                    "0",
                    "-i",
                    str(list_file),
                    "-c",
                    "copy",
                    str(output),
                ],
                check=True,
                capture_output=True,
            )
            list_file.unlink(missing_ok=True)
        else:
            # Último recurso: solo el primer chunk (avisar)
            temp_files[0].replace(output)
            print(f"  AVISO: texto muy largo, pydub/ffmpeg no disponible; parte truncada -> {output.name}")

    for temp in temp_files:
        if temp.exists() and temp != output:
            temp.unlink(missing_ok=True)


async def main() -> int:
    try:
        import edge_tts  # noqa: F401
    except ImportError:
        print("Instala: pip install edge-tts pydub")
        return 1

    catalog = load_json(TOURS_JSON)
    total = 0
    errors = 0

    for tour in catalog["tours"]:
        tour_id = tour["id"]
        script_name = SCRIPT_FILE_BY_TOUR.get(tour_id)
        if not script_name:
            print(f"Sin guión para tour {tour_id}")
            continue

        script_path = SCRIPTS_DIR / script_name
        if not script_path.exists():
            print(f"Falta {script_path}")
            errors += 1
            continue

        pack = load_json(script_path)
        narrations = {s["stopId"]: normalize_text(s["narration"]) for s in pack["stops"]}
        rate = RATE_BY_TOUR.get(tour_id, "+0%")

        print(f"\n=== {tour['title']} ({tour_id}) voz={VOICE} rate={rate} ===")

        for stop in tour["stops"]:
            stop_id = stop["id"]
            audio_rel = stop["audioAsset"]
            text = narrations.get(stop_id)
            if not text:
                print(f"  SKIP {stop_id}: sin narración en guión")
                errors += 1
                continue

            out = ASSETS / audio_rel
            print(f"  [{stop['order']:02d}] {out.name} ({len(text)} chars)...", end=" ", flush=True)

            try:
                await synthesize_to_mp3(text, out, rate)
                size_kb = out.stat().st_size // 1024
                print(f"OK ({size_kb} KB)")
                total += 1
            except Exception as exc:
                print(f"ERROR: {exc}")
                errors += 1

    print(f"\nListo: {total} archivos generados, {errors} errores.")
    print(f"Carpeta base: {ASSETS / 'audio'}")
    return 0 if errors == 0 else 1


if __name__ == "__main__":
    sys.exit(asyncio.run(main()))
