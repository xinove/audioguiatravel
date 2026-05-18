import json
import os

BASE = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "scripts")


def pack(tour_id, stops):
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


def write(name, data):
    path = os.path.join(BASE, name)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    print("wrote", path)


# NEWTOWN
newtown = [
    stop(
        "nt-01",
        "Princes Street Gardens (este)",
        """¡Hola! Soy Margaret Reid, modista de 1820. Debajo de tus pies hubo el Nor Loch; hoy hay jardines y vistas al castillo como teatro iluminado. En verano, sombrillas de colores; en invierno, risas en el césped resbaladizo. El optimismo también huele a café de Princes Street.""",
        "Pájaros, risas, fontana.",
        "Nor Loch antes del drenaje.",
        300,
    ),
    stop(
        "nt-02",
        "Scott Monument",
        """¡Qué torre tan dramática! Walter Scott hizo llorar a señoras con buen gusto. El monumento es gótico y perfecto, como un vestido con encaje de más. Desde abajo imagino capítulos dictados bajo la lluvia. Si el viento silba, la ciudad lee en voz alta.""",
        "Viento en piedra.",
        "Monumento a Scott.",
        330,
    ),
    stop(
        "nt-03",
        "George Street",
        """George Street: columna vertebral elegante. Fachadas gemelas, banqueros con sombrero, telas en escaparates que reflejan la calle dos veces. Piano en un piso alto, carcajadas en un club. Esta calle declama planes, no susurra tragedias.""",
        "Vals lejano, carruajes.",
        "George Street con gas.",
        330,
    ),
    stop(
        "nt-04",
        "Charlotte Square",
        """Geometría pura: regalo de caja para la vista. Árboles que vieron propuestas y contratos de seda. Puertas azules, modales de Londres y deudas elegantes. En 1820 se hablaba de ferrocarriles futuros; hoy del tiempo, con la misma cortesía.""",
        "Tazas, hojas.",
        "Charlotte Square.",
        330,
    ),
    stop(
        "nt-05",
        "Moray Place",
        """Un círculo de columnas que abraza la calle. Vecinos que se saludan por el perro, niños con tribunales de gatos inocentes. Grita hola: el eco te devuelve un hola limpio.""",
        "Eco suave.",
        "Moray Place.",
        300,
    ),
    stop(
        "nt-06",
        "Stockbridge",
        """El barrio que sonríe temprano. Mercado de pan, queso y flores. El puente une orillas; los pescadores exageran y las floristas corrigen el precio. Deja una moneda al músico: es banda sonora.""",
        "Mercado, violín.",
        "Stockbridge market.",
        360,
    ),
    stop(
        "nt-07",
        "Dean Village",
        """Cuento de hadas con molinos de verdad. Agua rápida, casas que se inclinan a susurrar. El musgo huele a lluvia antigua y nueva.""",
        "Agua, pájaros.",
        "Dean Village.",
        390,
    ),
    stop(
        "nt-08",
        "Calton Hill",
        """Balcón de Edimburgo. El Firth brilla; Nelson y Playfair enseñan a mirar lejos. Trae chaqueta: el viento es poeta sin permiso.""",
        "Viento, gaviotas.",
        "Vista Calton Hill.",
        360,
    ),
    stop(
        "nt-09",
        "Broughton Street",
        """Cafés y tertulias. Artistas y abogados comparten mesa. En 1820 ya había conversación larga; hoy hay Wi-Fi y las mismas risas.""",
        "Café.",
        "Broughton.",
        330,
    ),
    stop(
        "nt-10",
        "St Andrew Square",
        """Cierre en la plaza financiera. Gracias por caminar conmigo. Mañana otra calle por coser con historias.""",
        "Ciudad lejana.",
        "St Andrew Square.",
        330,
    ),
]
write("newtown-cheerful.json", pack("newtown-cheerful", newtown))

ghosts = []
for sid, title, core, sfx, cap, sec in [
    (
        "gh-01",
        "Greyfriars Kirkyard",
        "La puerta de Greyfriars suspira. Soy el vigilante sin nombre. Las lápidas inclinadas guardan nombres que el viento aún pronuncia. Un perro espera a un dueño que no volverá: leyenda viva, no adorno.",
        "Viento, lechuza.",
        "Cementerio de noche.",
        420,
    ),
    (
        "gh-02",
        "Covenanters' Prison",
        "Cadenas que aprendieron nombres. Cientos respiraron el mismo aire húmedo bajo la iglesia. A veces el conteo llega a diez sin nadie visible.",
        "Cadenas, gemido.",
        "Prisión.",
        450,
    ),
    (
        "gh-03",
        "Grassmarket",
        "Plaza de la horca. El público aplaudía; hoy el silencio aplaude de vuelta. El aire pesa como tela mojada.",
        "Multitud fantasma.",
        "Horca.",
        480,
    ),
    (
        "gh-04",
        "Victoria Street",
        "Espiral hacia Candlemaker Row. Sombras con pasos que no son tuyos. Sigue de frente.",
        "Pasos eco.",
        "Curva nocturna.",
        420,
    ),
    (
        "gh-05",
        "West Bow",
        "Thomas Weir amaba el fuego demasiado. Si hueles humo sin llama, pasa cerca.",
        "Chispa.",
        "Casa Weir.",
        450,
    ),
    (
        "gh-06",
        "Lauriston Close",
        "Puertas que ceden al peso del tiempo. No entres si oyes tu nombre de niño.",
        "Bisagra.",
        "Close.",
        420,
    ),
    (
        "gh-07",
        "Edinburgh Vaults (exterior)",
        "Bajo South Bridge, ciudad invertida. Doce arcos, doce bocas frías.",
        "Goteo, risa.",
        "Bóvedas.",
        480,
    ),
    (
        "gh-08",
        "Niddry Street",
        "Tu eco tiene prisa. Alguien corre en la pared.",
        "Correr.",
        "Niddry.",
        450,
    ),
    (
        "gh-09",
        "Blair Street Underground",
        "Gritos lejanos y violín desafinado. No es para bailar.",
        "Violín.",
        "Túnel.",
        480,
    ),
    (
        "gh-10",
        "Cowgate",
        "Bares cerrados, historias abiertas. Si tintinea una copa sola, brinda por los que no volvieron.",
        "Vaso.",
        "Cowgate.",
        450,
    ),
    (
        "gh-11",
        "South Bridge (arcos)",
        "Doce arcos, doce susurros. Cuenta hasta doce. No llegues a trece.",
        "Eco.",
        "Arcos.",
        420,
    ),
    (
        "gh-12",
        "Castle Esplanade (noche)",
        "El castillo duerme con un ojo abierto. Yo me quedo; tú vete antes de que la niebla cierre el camino.",
        "Viento, tambor lejano.",
        "Explanada lunar.",
        480,
    ),
]:
    ghosts.append(
        stop(
            sid,
            title,
            core + " No mires demasiado las ventanas oscuras: a veces miran de vuelta.",
            sfx,
            cap,
            sec,
        )
    )
write("oldtown-ghosts.json", pack("oldtown-ghosts", ghosts))

arthur = [
    stop(
        "as-01",
        "Holyrood Park gates",
        "Soy Fiona MacLeod, montañesa de 1890. Subimos donde el dragón dormido guarda la ciudad. Volcán apagado con hierba encima.",
        "Viento.",
        "Puertas.",
        300,
    ),
    stop(
        "as-02",
        "St Margaret's Loch",
        "Espejo que no miente. Reinasa y cisnes comparten orilla.",
        "Agua.",
        "Loch.",
        360,
    ),
    stop(
        "as-03",
        "Dunsapie Loch",
        "Colina sagada. Antiguas hogueras de Samhain en el horizonte.",
        "Brisa.",
        "Loch.",
        330,
    ),
    stop(
        "as-04",
        "Salisbury Crags (base)",
        "Roca partida: gigantes o hielo, ambos cuentan verdad.",
        "Piedra.",
        "Crags.",
        390,
    ),
    stop(
        "as-05",
        "Radical Road",
        "Sendero tallado por jóvenes que pedían dignidad con vistas.",
        "Pico.",
        "Sendero.",
        360,
    ),
    stop(
        "as-06",
        "Arthur's Seat summit approach",
        "Último tramo. La ciudad se encoge para que crezcas un metro.",
        "Viento.",
        "Ladera.",
        420,
    ),
    stop(
        "as-07",
        "Summit (Arthur's Seat)",
        "360 grados de reino. Mar, ciudad, piedra volcánica.",
        "Cumbre.",
        "Panorámica.",
        450,
    ),
    stop(
        "as-08",
        "Descent to Queen's Drive",
        "Bajada amable. Hasta la próxima leyenda.",
        "Grava.",
        "Bajada.",
        360,
    ),
]
write("arthur-holyrood.json", pack("arthur-holyrood", arthur))
