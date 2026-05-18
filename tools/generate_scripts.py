"""Genera guiones JSON para las audioguías. Ejecutar: python tools/generate_scripts.py"""
import json
import os

BASE = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "scripts")


def pack(tour_id: str, stops: list) -> dict:
    return {"tourId": tour_id, "language": "es", "stops": stops}


def stop(sid, title, narration, sfx="", caption="", secs=360):
    return {
        "stopId": sid,
        "title": title,
        "narration": narration.strip(),
        "sfxNotes": sfx,
        "imageCaption": caption,
        "estimatedSpokenSeconds": secs,
    }


def main():
    os.makedirs(BASE, exist_ok=True)
    # oldtown already generated; regenerate all for consistency
    from importlib import import_module

    import_module("generate_scripts_data")  # noqa: if we split - inline below

    exec(open(os.path.join(os.path.dirname(__file__), "generate_scripts_data.py"), encoding="utf-8").read())


if __name__ == "__main__":
    main()
