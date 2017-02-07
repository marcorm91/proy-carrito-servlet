-- Schema: bd_marcogames

-- DROP SCHEMA bd_marcogames;

CREATE SCHEMA bd_marcogames
  AUTHORIZATION amromero;

COMMENT ON SCHEMA bd_marcogames
  IS 'Carrito de la compra.';
  
  -- Table: bd_marcogames.juegos

-- DROP TABLE bd_marcogames.juegos;

CREATE TABLE bd_marcogames.juegos
(
  id serial NOT NULL,
  nombre character varying,
  desc_es character varying,
  desc_en character varying,
  precio real,
  cantidad integer,
  "rutaImg" character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bd_marcogames.juegos
  OWNER TO amromero;

-- Juegos de prueba en la BD

INSERT INTO bd_marcogames.juegos(
            id, nombre, desc_es, desc_en, precio, cantidad, "rutaImg")
    VALUES (1, 'Battlefield 4', 'Lánzate a la guerra total del modo multijugador de Battlefield 4. Con capacidad para 64 jugadores y 7 modos de juego únicos disponibles en 10 mapas enormes, no hay nada que pueda compararse a la escala y el alcance de Battlefield 4.', '-', 60, 200, 'recursos/Imagenes/b4.jpg');

INSERT INTO bd_marcogames.juegos(
            id, nombre, desc_es, desc_en, precio, cantidad, "rutaImg")
    VALUES (2, 'Call Of Duty', 'Call of Duty®: Infinite Warfare te llevará en un viaje inolvidable mientras luchas contra un implacable enemigo que amenaza nuestro modo de vida desde la Tierra hasta más allá de la atmósfera.', '-', 50, 140, 'recursos/Imagenes/cod.jpg');

INSERT INTO bd_marcogames.juegos(
            id, nombre, desc_es, desc_en, precio, cantidad, "rutaImg")
    VALUES (3, 'Destiny', 'La muralla, que durante siglos ha resistido a lo largo de la frontera sur de la Antigua Rusia, ha sido derribada en la interminable guerra contra nuestros enemigos. Los mutantes saquean los sepulcros de la Edad de Oro, pero el mal que han desenterrado de entre los escombros es más peligroso de lo que pueden comprender.', '-', 55, 0, 'recursos/Imagenes/destiny.jpg');