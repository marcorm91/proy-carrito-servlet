package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.MJuegos;

public class CPrincipal {
	
	private Connection conexion;
	private MJuegos modeloJuegos;
	private Object[][] juego;
	
	/**
	 * Constructor.
	 */
	public CPrincipal(){
		conexionBD();
		modeloJuegos = new MJuegos(conexion);
	}
	
	/**
	 * Conexión a la BD de clase.
	 */
	private void conexionBD() {
		String bd =   "jdbc:postgresql://ns3034756.ip-91-121-81.eu/amromero";
        String user = "amromero";
        String pass = "amromero";
        
        try {
            conexion = DriverManager.getConnection(bd, user, pass);
            System.err.println("Conexión BD realizada.");
        } catch (SQLException ex) {
            System.err.println(ex);
        } 
	}

	
	/**
	 *  Obtenemos el total de juegos de la BD en un array bidimensional. 
	 * @return
	 */
	public Object[][] totalJuegos() {
		juego = modeloJuegos.muestraTotal();
		return juego;
	}

	
	/**
	 * 
	 * @param listaCarrito
	 * @return
	 */
	public Object[][] listaJuegosCarrito(ArrayList<String> listaCarrito) {
		juego = modeloJuegos.muestraTotal(listaCarrito);
		return juego;
	}

	
	/**
	 * Método que restará de la BD un juego añadido a la cesta.  Necesitamos comprobar que
	 * la cantidad de existencias sobre un determinado juego es 0 o mayor que el mismo.
	 * @param idJuego
	 * @return
	 */
	public boolean restaTabla(String idJuego) {
		int cantidad = 0;
		cantidad = modeloJuegos.devuelveCantidad(idJuego);
		
		if(cantidad <= 0){
			return false;
		}else{		
			modeloJuegos.eliminaJuego(idJuego);
			return true;
		}
	}

	
	/**
	 * Cuando eliminamos un juego de la cesta necesitamos rescatar el juego y añadirlo nuevamente
	 * a la BD.
	 * @param idJuego
	 */
	public void sumaTabla(String idJuego) {
		modeloJuegos.aniadeJuego(idJuego);
	}

}
