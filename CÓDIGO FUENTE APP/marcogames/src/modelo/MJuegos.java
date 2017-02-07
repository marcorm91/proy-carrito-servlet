package modelo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class MJuegos {
	
	private final Connection conexion;
	
	public MJuegos(Connection conexion){
		this.conexion = conexion;
	}
	
	/**
	 * Este método nos devolverá el total de juegos en la base de datos para mostrar en la página
	 * principal.  Lo ideal sería poner los 4 o 5 juegos más novedosos limitando esa lista a 4 ó 5 elementos.
	 * En nuestro caso, sólo tenemos 3 juegos en BD, por lo que nos devolverá 3.
	 * @return Filas: Devuelve el total de filas de la BD.
	 */
	private int totalJuegos(){
		
		String total = "select count(*) as contador from bd_marcoGames.juegos;";
		int filas = 0;
		
		try{
			 PreparedStatement sentencia = conexion.prepareStatement(total);
			 ResultSet rs = sentencia.executeQuery();
			 
			 while(rs.next()){
				filas = rs.getInt("contador");
			 }

		}catch(Exception e){
			System.out.println(e);
		}
					 
		return filas;
	}

	/**
	 * Nos devuelve el total de juegos que tiene la BD para mostrar en la pantalla principal.
	 * Para ello, disponemos de un array bidimensional donde obtenemos la posición del juego en la BD
	 * con su nombre, precio, cantidad, etc.
	 * @return Object[][]: Devuelve el juego en un array bidimensional con todas su características.
	 */
	public Object[][] muestraTotal(){
		
		int cantidad = totalJuegos();
		
		Object datos[][] = new Object[cantidad][7];
		int i = 0;
		
		String selectJuegos = "select * from bd_marcoGames.juegos order by id;";
		
		 try{
			 
			 PreparedStatement sentencia = conexion.prepareStatement(selectJuegos);
			 ResultSet rs = sentencia.executeQuery();
	            
	            while(rs.next()){
	                datos[i][0] = rs.getString("id");
	                datos[i][1] = rs.getString("nombre");
	                datos[i][2] = rs.getString("desc_es");
	                datos[i][3] = rs.getString("desc_en");
	                datos[i][4] = rs.getString("precio");
	                datos[i][5] = rs.getString("cantidad");
	                datos[i][6] = rs.getString("rutaImg");                
	                i++;
	            }
	            
		 }catch(Exception e){
	    	 System.out.println(e);
	     }
        
        return datos;
	}

	/**
	 * Nos devuelve los elementos que se encuentran en el carrito.  Para ello le pasamos el número de
	 * elementos que hay en la lista para así poder asignarle tamaño al array bidimensional.
	 * @param listaCarrito: Para obtener el tamaño de la lista del carrito.
	 * @return Nos devuelve los objetos que coincidan en la BD sobre la lista.
	 */
	public Object[][] muestraTotal(ArrayList<String> listaCarrito) {
		
		Object datos[][] = new Object[listaCarrito.size()][7];		
		int i;
		
		
		for(i = 0; i < listaCarrito.size(); i++){
		
		String selectJuegos = "select * from bd_marcoGames.juegos where id = "+listaCarrito.get(i)+" order by id";
		
		 try{
			 
			 PreparedStatement sentencia = conexion.prepareStatement(selectJuegos);
			 ResultSet rs = sentencia.executeQuery();
	          
	            while(rs.next()){
	                datos[i][0] = rs.getString("id");
	                datos[i][1] = rs.getString("nombre");
	                datos[i][2] = rs.getString("desc_es");
	                datos[i][3] = rs.getString("desc_en");
	                datos[i][4] = rs.getString("precio");
	                datos[i][5] = rs.getString("cantidad");
	                datos[i][6] = rs.getString("rutaImg");                
	            }
	            
		 }catch(Exception e){
	    	 System.out.println(e);
	     }
		 
		}
        
        return datos;
	}

	/**
	 * Eliminamos de la tabla un elemento por cada añadido a la cesta.
	 * @param idJuego
	 */
	public void eliminaJuego(String idJuego) {
		
		String eliminaJuego = "update bd_marcoGames.juegos set cantidad = (cantidad - 1) where id = ?; ";
		
		 try{
             PreparedStatement sentencia = conexion.prepareStatement(eliminaJuego);
             sentencia.setInt(1, Integer.valueOf(idJuego));
             sentencia.executeUpdate();
		 }catch(Exception e){
			 System.out.println(e);
		 }
		
	}

	/**
	 * Añadimos a la tabla nuevamente el juego que fue eliminado de la cesta.
	 * @param idJuego
	 */
	public void aniadeJuego(String idJuego) {
				
		String aniadeJuego = "update bd_marcoGames.juegos set cantidad = (cantidad + 1) where id = ?; ";
		
		 try{
            PreparedStatement sentencia = conexion.prepareStatement(aniadeJuego);
            sentencia.setInt(1, Integer.valueOf(idJuego));
            sentencia.executeUpdate();
		 }catch(Exception e){
			 System.out.println(e);
		 }
		
	}


	/**
	 * Nos devuelve la cantidad de un juego en concreto para que éste pueda ser añadido o no a la cesta
	 * de la compra.
	 * @param idJuego
	 * @return
	 */
	public int devuelveCantidad(String idJuego) {
		
		String cantidad = "select cantidad as cant from bd_marcoGames.juegos where id = "+idJuego+";";
		int total = 0;
		
		 try{
           PreparedStatement sentencia = conexion.prepareStatement(cantidad);
           ResultSet rs = sentencia.executeQuery();
           
           while(rs.next()){
        	   total = rs.getInt("cant");
           }

		 }catch(Exception e){
			 System.out.println(e);
		 }
		 		
		return total;
	}

}
