"""
Construye guiones con entrada, cuerpo y salida para cada parada.
Uso: python tools/build_engaging_scripts.py
"""
from __future__ import annotations

import json
import os
import re
from pathlib import Path

BASE = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "scripts"
TOURS = Path(__file__).resolve().parent.parent / "app" / "src" / "main" / "assets" / "tours" / "edinburgh.json"


def pack(tour_id: str, stops: list) -> dict:
    return {"tourId": tour_id, "language": "es", "stops": stops}


def entry(
    stop_id: str,
    title: str,
    intro: str,
    body: str,
    outro: str,
    sfx: str = "",
    caption: str = "",
) -> dict:
    narration = " ".join(p.strip() for p in (intro, body, outro) if p.strip())
    words = len(narration.split())
    secs = max(90, int(words / 2.3))  # ~140 palabras/min
    return {
        "stopId": stop_id,
        "title": title,
        "intro": intro.strip(),
        "body": body.strip(),
        "outro": outro.strip(),
        "narration": narration,
        "sfxNotes": sfx,
        "imageCaption": caption,
        "estimatedSpokenSeconds": secs,
    }


def load_oldtown_bodies() -> dict[str, str]:
    path = BASE / "oldtown-historic.json"
    data = json.loads(path.read_text(encoding="utf-8"))
    return {s["stopId"]: s["narration"] for s in data["stops"]}


# --- Plantillas Old Town (James Stuart) ---
OLDTOWN_INTRO_FIRST = (
    "¡Ah, por fin llegaste! Bienvenido a mi Edimburgo, el de piedra, niebla y cuentos que no caben en un pergamino. "
    "Soy James Stuart, escribano del invierno de mil setecientos cuarenta y cinco. "
    "Pon los auriculares, respira hondo y no corras: la ciudad se ofende si la tratas como aeropuerto. "
    "Esta es la parada uno."
)

OLDTOWN_INTROS = [
    "¡Bienvenido de nuevo! Me alegra verte por aquí — pensé que te habías escapado a por un scone. "
    "Retomamos la historia justo donde la piedra guarda secretos.",
    "¿Otra vez tú? Qué bien. La ciudad se aburre sin oyentes. Colócate cómodo, esto se pone bueno.",
    "Continúa un poco más abajo, como diría mi madre, y aquí estoy yo para contarte lo que los folletos turísticos callan.",
    "Sigues despierto. Me gusta tu estilo. Otra parada, otro capítulo de humo y leyenda.",
    "¡Hola otra vez! Si llevas prisa, respira igual: Edimburgo castiga a quien solo mira el móvil.",
    "Bienvenido de vuelta al camino. Las gaviotas ya te reconocen — o eso quiero creer.",
    "¿Seguimos? Perfecto. La próxima anécdota me costó dos cenas y una disculpa pública.",
    "Casi al final del recorrido. No te rindas ahora: lo mejor de la milla real está a la vuelta.",
    "Última parada de este capítulo. Camina conmigo un poco más y cerramos como se cierra un buen libro.",
]

OLDTOWN_OUTROS = [
    "Bien, hasta aquí esta parada. Echa a andar un minuto, sigue bajando la calle, y cuando llegues a {next}, abre la siguiente. Te espero allí — no tardes, que las historias se enfrían.",
    "Eso es todo por aquí. Continúa más abajo, sin prisa pero sin pausa. La siguiente se llama {next}. Si te pierdes, pregúntale a una gaviota: son malas consejeras pero honestas.",
    "Fin de esta escena. Guarda el móvil un segundo, mira las piedras, y cuando veas {next}, pulsa la siguiente parada. Prometo que la anécdota merece el escalón.",
    "Cerramos este tramo. Sigue el empedrado, respira el viento, y en {next} seguimos. Si alguien te ofrece un fantasma de oferta, rechaza: el mío es más barato.",
    "Hasta pronto en la próxima. Camina un poco, disfruta el ruido de la ciudad, y cuando estés en {next}, vuelve a mí. No hace falta aplaudir, solo no perderte.",
    "Parada completada. Continúa el paseo — la historia no tiene GPS, pero tú sí. Siguiente destino: {next}.",
    "Y ahora, a la siguiente. No te vayas a Instagram todavía: en {next} hay sangre real y un poco de humor negro.",
    "Queda poco para el final del tour. Sigue hacia {next} y no pares el audio en mitad de un secreto, que da mala suerte.",
    "Última parada después de esta. Disfruta el camino hasta {next}; luego te cuento cómo se despide un escribano.",
]

OLDTOWN_OUTRO_LAST = (
    "Y aquí termina nuestro paseo por la Old Town, al menos por hoy. "
    "Gracias por escucharme, por caminar despacio y por no tropezar con más de dos veces — récord personal de visitante. "
    "Si vuelves mañana, la ciudad te recordará: huele a lluvia, a historia y a ganas de otra vuelta. "
    "Soy James Stuart. Buen viaje, buen Edimburgo, y cuidado con las gaviotas: son ladronas con alas."
)


def build_oldtown() -> list:
    bodies = load_oldtown_bodies()
    catalog = json.loads(TOURS.read_text(encoding="utf-8"))
    tour = next(t for t in catalog["tours"] if t["id"] == "oldtown-historic")
    stops_meta = sorted(tour["stops"], key=lambda s: s["order"])
    result = []
    for i, meta in enumerate(stops_meta):
        sid = meta["id"]
        title = meta["title"]
        body = bodies[sid]
        if i == 0:
            intro = OLDTOWN_INTRO_FIRST
        else:
            intro = OLDTOWN_INTROS[(i - 1) % len(OLDTOWN_INTROS)]
        if i < len(stops_meta) - 1:
            nxt = stops_meta[i + 1]["title"]
            outro = OLDTOWN_OUTROS[i % len(OLDTOWN_OUTROS)].format(next=nxt)
        else:
            outro = OLDTOWN_OUTRO_LAST
        result.append(
            entry(sid, title, intro, body, outro, sfx="Voz calmada, ambiente de época.", caption=title)
        )
    return result


# --- New Town (Margaret Reid, tono alegre) ---
NEWTOWN_FIRST = (
    "¡Hola, hola! Bienvenida, bienvenido — no importa el tiempo, aquí siempre hay estilo. "
    "Soy Margaret Reid, modista de mil ochocientos veinte, y hoy te robo una hora para enseñarte la ciudad que se vistió de neoclásico y optimismo. "
    "Ponte cómoda, que esta New Town brilla incluso con lluvia fina. ¡Empezamos!"
)

NEWTOWN_INTROS = [
    "¡Qué alegría verte otra vez por aquí! ¿Seguimos? La ciudad nueva no espera a nadie, pero a ti te hago una excepción.",
    "Bienvenido de nuevo. Respira: esto no es la Old Town oscura — aquí hasta el viento lleva corbata.",
    "Continúa un poco más, como las damas bien educadas, y llegamos a otro rincon con historia y buen gusto.",
    "¿Sigues conmigo? Perfecto. Otra parada, otro capítulo de luces, columnas y chismes elegantes.",
    "¡Hola otra vez! Si te caíste en Dean Village, no pasa nada: hasta los locales tropezamos por admirar.",
    "Me alegra que no te hayas ido a por otro café. Aún quedan secretos con olor a pan recién horneado.",
    "Casi terminamos el paseo. Mantén el ritmo alegre — el final merece una sonrisa.",
    "Penúltima parada del tour. Después queda la despedida con vistas.",
    "Último tramo. Camina conmigo y cerramos con broche de oro georgiano.",
]

NEWTOWN_OUTRO_LAST = (
    "Y con esto cerramos la New Town por hoy. Gracias por caminar conmigo, por reír un poco y por no pisar el césped donde no toca — heroína, héroe. "
    "Si vuelves, ponte algo bonito: la ciudad siempre está de gala. Hasta pronto, querida alma curiosa."
)

NEWTOWN_BODIES = [
    """Debajo de tus pies hubo un lago llamado Nor Loch; hoy hay jardines y el castillo al fondo como telón de teatro. En verano, sombrillas de colores; en invierno, risas resbalando con dignidad. El optimismo también huele a café de Princes Street.""",
    """¡Qué torre tan dramática! El monumento a Scott es gótico, exagerado y perfecto, como un vestido con demasiado encaje que funciona igual. Walter Scott hizo llorar a señoras con buen gusto. Si el viento silba, la ciudad lee en voz alta.""",
    """George Street es la columna vertebral elegante: fachadas gemelas, banqueros con sombrero, escaparates que reflejan la calle dos veces. En un piso alto alguien practica piano; en un club, carcajadas. Esta calle declama planes, no susurra tragedias.""",
    """Charlotte Square es geometría pura, un regalo de caja para la vista. Árboles que vieron propuestas de matrimonio y contratos de seda. Puertas azules, modales de Londres y deudas con buenos modales.""",
    """Moray Place es un círculo de columnas que abraza la calle. Vecinos que se saludan por el perro; niños que juzgan gatos inocentes en tribunales improvisados. Grita hola si quieres: el eco te devuelve un hola limpio.""",
    """Stockbridge sonríe temprano. Mercado de pan, queso y flores; pescadores que exageran; floristas que corrigen el precio con una sonrisa. Deja una moneda al músico callejero: es la banda sonora oficial.""",
    """Dean Village parece sacado de un cuento: molinos, agua clara, casas que se inclinan a susurrar. El musgo huele a lluvia antigua y nueva a la vez. Ideal para fotos; malo para ir con prisa.""",
    """Calton Hill es el balcón de Edimburgo. El Firth brilla; monumentos enseñan a mirar lejos. Trae chaqueta: el viento es poeta y no pide permiso.""",
    """Broughton Street vibra con cafés y tertulias. Artistas y abogados comparten mesa; todos fingen no mirar el pastel del vecino.""",
    """St Andrew Square cierra el circuito con elegancia financiera y bullicio amable. La ciudad moderna te devuelve al presente con gracia.""",
]


def build_newtown() -> list:
    catalog = json.loads(TOURS.read_text(encoding="utf-8"))
    tour = next(t for t in catalog["tours"] if t["id"] == "newtown-cheerful")
    stops_meta = sorted(tour["stops"], key=lambda s: s["order"])
    result = []
    for i, meta in enumerate(stops_meta):
        body = NEWTOWN_BODIES[i]
        intro = NEWTOWN_FIRST if i == 0 else NEWTOWN_INTROS[(i - 1) % len(NEWTOWN_INTROS)]
        if i < len(stops_meta) - 1:
            outro = (
                f"Listo por aquí. Sigue paseando hasta {stops_meta[i + 1]['title']} y pulsa la siguiente parada. "
                "Si te pierdes, no preguntes al primer señor con barba: siempre manda a la taberna."
            )
        else:
            outro = NEWTOWN_OUTRO_LAST
        result.append(entry(meta["id"], meta["title"], intro, body, outro, sfx="Ambiente alegre, ligero."))
    return result


# --- Ghosts ---
GHOST_FIRST = (
    "Bienvenido… o debería decir: bienvenido si insistes. Soy el vigilante sin nombre, de mil seiscientos ochenta, "
    "y esta noche la Old Town susurra cosas que no van en folletos. "
    "No corras. No mires demasiado atrás. Y si oyes pasos que no son los tuyos… es normal. Empezamos."
)

GHOST_INTROS = [
    "Has vuelto. Bien. La niebla te estaba esperando — qué halago, ¿no?",
    "Continúa. Más abajo. La ciudad enterrada no muerde… casi nunca.",
    "¿Sigues aquí? Valiente. O imprudente. En ambos casos, adelante.",
    "Otra parada, otro susurro. No apagues el audio: aquí el silencio también habla.",
    "Bienvenido de nuevo al camino oscuro. Las lápidas te conocen ya.",
    "Siguiente capítulo de sombras. Respira bajo, que el miedo ocupa menos si lo dejas salir.",
    "Casi al final del recorrido. No te vayas a dormir todavía.",
    "Penúltima parada. Lo peor — o lo mejor — está cerca.",
    "Última escena de esta noche. Escucha hasta el final.",
]

GHOST_OUTRO_LAST = (
    "Aquí termina el paseo de fantasmas, por ahora. Gracias por no gritar demasiado fuerte — asustas a los fantasmas reales. "
    "Vuelve a casa con calma. Si una puerta se cierra sola detrás de ti, no es despedida: es costumbre. Buenas noches."
)

GHOST_BODIES = [
    """Greyfriars: la puerta no cruje, suspira. Lápidas inclinadas, nombres que el viento aún pronuncia. Un perro espera a un dueño que no volverá — leyenda viva, no adorno turístico.""",
    """La prisión de los covenanters: cadenas que aprendieron nombres. Cientos respiraron el mismo aire húmedo bajo la iglesia.""",
    """Grassmarket: donde el público aplaudía la horca. El silencio de hoy también aplaude, pero al revés.""",
    """Victoria Street en espiral: sombras que suben. Sigue de frente. No corras — correr atrae curiosos invisibles.""",
    """West Bow y la leyenda de Thomas Weir: si hueles humo sin llama, él pasa cerca.""",
    """Lauriston Close: puertas que ceden al peso del tiempo. No entres si oyes tu nombre de niño.""",
    """Bajo South Bridge, la ciudad invertida. Doce arcos, doce bocas frías.""",
    """Niddry Street: tu eco tiene prisa. Alguien corre en la pared.""",
    """Blair Street: gritos lejanos y violín desafinado. No es música para bailar.""",
    """Cowgate: bares cerrados, historias abiertas. Si tintinea una copa sola, brinda por los que no volvieron.""",
    """South Bridge: doce arcos, doce susurros. Cuenta hasta doce. No llegues a trece.""",
    """La explanada del castillo de noche: el castillo duerme con un ojo abierto. Yo me quedo; tú vete antes de que la niebla cierre el camino.""",
]


def build_ghosts() -> list:
    catalog = json.loads(TOURS.read_text(encoding="utf-8"))
    tour = next(t for t in catalog["tours"] if t["id"] == "oldtown-ghosts")
    stops_meta = sorted(tour["stops"], key=lambda s: s["order"])
    result = []
    for i, meta in enumerate(stops_meta):
        body = GHOST_BODIES[i] + " No mires demasiado las ventanas oscuras: a veces miran de vuelta."
        intro = GHOST_FIRST if i == 0 else GHOST_INTROS[(i - 1) % len(GHOST_INTROS)]
        if i < len(stops_meta) - 1:
            outro = (
                f"Hasta aquí. Camina hacia {stops_meta[i + 1]['title']} y activa la siguiente parada. "
                "Si tu sombra va más rápido que tú, no es broma: es invitación."
            )
        else:
            outro = GHOST_OUTRO_LAST
        result.append(entry(meta["id"], meta["title"], intro, body, outro, sfx="Suspense, puertas, viento."))
    return result


# --- Arthur ---
ARTHUR_FIRST = (
    "¡Arriba, explorador! Soy Fiona MacLeod, montañesa de mil ochocientos noventa, "
    "y hoy conquistamos la colina sagrada donde el dragón dormido guarda Edimburgo. "
    "Calzado cómodo, agua y sentido del humor: el viento arriba no perdona vanidad. ¡Vamos!"
)

ARTHUR_INTROS = [
    "¡Bienvenido de nuevo! ¿Respiraste? Bien. Seguimos subiendo historia.",
    "Continúa el sendero. La colina premia a quien no se rinde.",
    "¿Sigues en pie? Eso merece un capítulo más.",
    "Otra parada, otra leyenda con olor a hierba y mar.",
    "Casi en la cumbre. No pares ahora.",
    "Último tramo antes del mirador. Vale la pena.",
    "La cima te espera. Despacio pero sin excusas.",
    "Bajamos pronto. Guarda energía para la despedida.",
]

ARTHUR_OUTRO_LAST = (
    "Fin del paseo por Arthur's Seat. Gracias por subir conmigo, por no quejarte más de tres veces — récord — "
    "y por mirar el horizonte como quien guarda un secreso bonito. Hasta la próxima leyenda."
)

ARTHUR_BODIES = [
    """Holyrood Park: entrada al parque real. Leyenda del dragón dormido bajo la hierba.""",
    """St Margaret's Loch: espejo que no miente. Cisnes, reinas y reflejos de cráter.""",
    """Dunsapie Loch: colina sagrada, hogueras antiguas en la memoria del lugar.""",
    """Salisbury Crags: roca partida — gigantes o hielo, ambos cuentan verdad.""",
    """Radical Road: sendero tallado por jóvenes que pedían dignidad con vistas de sobra.""",
    """Último tramo antes de la cumbre. El viento decide quién manda hoy.""",
    """La cumbre: trescientos sesenta grados de reino. Mar, ciudad, piedra volcánica.""",
    """Bajada tranquila por Queen's Drive. Las piernas protestan; el corazón, no.""",
]


def build_arthur() -> list:
    catalog = json.loads(TOURS.read_text(encoding="utf-8"))
    tour = next(t for t in catalog["tours"] if t["id"] == "arthur-holyrood")
    stops_meta = sorted(tour["stops"], key=lambda s: s["order"])
    result = []
    for i, meta in enumerate(stops_meta):
        body = ARTHUR_BODIES[i]
        intro = ARTHUR_FIRST if i == 0 else ARTHUR_INTROS[(i - 1) % len(ARTHUR_INTROS)]
        if i < len(stops_meta) - 1:
            outro = (
                f"Parada lista. Sigue hasta {stops_meta[i + 1]['title']} y pulsa siguiente. "
                "Si el viento empuja, no discutas: siempre gana."
            )
        else:
            outro = ARTHUR_OUTRO_LAST
        result.append(entry(meta["id"], meta["title"], intro, body, outro, sfx="Viento, naturaleza."))
    return result


def write(name: str, data: dict) -> None:
    path = BASE / name
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")
    print("wrote", path, f"({len(data['stops'])} stops)")


def main() -> None:
    write("oldtown-historic.json", pack("oldtown-historic", build_oldtown()))
    write("newtown-cheerful.json", pack("newtown-cheerful", build_newtown()))
    write("oldtown-ghosts.json", pack("oldtown-ghosts", build_ghosts()))
    write("arthur-holyrood.json", pack("arthur-holyrood", build_arthur()))
    print("Done.")


if __name__ == "__main__":
    main()
