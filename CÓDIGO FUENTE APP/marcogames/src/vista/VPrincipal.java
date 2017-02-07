package vista;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import controlador.CPrincipal;

/**
 * Servlet implementation class VPrincipal
 */
@WebServlet("marcogames")
public class VPrincipal extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> listaCarrito;
	private PrintWriter out;
	private CPrincipal controlador;
	private HttpSession hs;
	private boolean existencia;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public VPrincipal() {
        controlador = new CPrincipal();
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		listaCarrito = new ArrayList<String>();
		  
		// Obtenemos la sesión del usuario visitante a la página web.
		hs = request.getSession();
		
		// Y establecemos un período máximo de inactividad sobre el mismo de una hora.
		hs.setMaxInactiveInterval(3600);
		
		// Recogemos todas las cookies del usuario y las metemos en un array de cookies.
		Cookie [] cookies = request.getCookies();
		
		boolean tieneCookie = false;
		
		// Si el array contiene al menos un elemento ...
		if(cookies != null){
			// Recorremos el array de cookies para comprobar la existencia del nombre de la misma.
			// Si encontramos una cookie con el nombre 'indice' quiere decir que el usuario ya la tiene,
			// luego tieneCookie será true.
			for (Cookie cookie: cookies){
				if(cookie.getName().equals("indice")){
					tieneCookie = true;
				}
			}
		}
		
		// Si el parámetro que recibe la web es nula quiere decir que el usuario simplemente puso
		// a224-pcb:8081 (o en su defecto www.marcogames.es) /marcogames ,es decir, a224-pcb:8081/marcogames
		if(request.getParameter("pagina") == null){
						
			// Creamos la cookie con el nombre 'indice' y ponemos una expiración de 1 día sobre la misma.
			Cookie mycookie = new Cookie("indice", "Pagina principal MarcoGAMES");
			mycookie.setMaxAge(86400);
			response.addCookie(mycookie);

			// Si el booleano tieneCookie establecido anteriormente para comprobar previamente si el usuario
			// dispone de la cookie no es true, quiere decir que no la tiene, luego muestra la página de bienvenida.
			if(!tieneCookie){
				htmlIndex();
			// En su defecto, redirige a la página principal de los juegos.
			}else{
				response.sendRedirect("marcogames?pagina=principal");
			}
			
		}else{
			
			// Si llegamos aquí quiere decir, que tenemos el parámetro pagina en la url a224-pcb:8081/marcogames?pagina
			switch(request.getParameter("pagina")){
			
				// En el caso de que sea ...?pagina=principal
				case "principal":
					
					// Para cada sesión crearemos un atributo llamado 'id'.
					// Si la sesión es nula pintamos el html principal de la web pasándole 0, que es la inicialización
					// del número de productos sobre la cesta.
					if(hs.getAttribute("carrito") == null){
						htmlPrincipal(0);
					}else{
						// De lo contrario, añadimos a la lista del carrito la sesión.
						listaCarrito = (ArrayList<String>) hs.getAttribute("carrito");
						htmlPrincipal(listaCarrito.size());
					}
				break;
				
				// Si el usuario entra en el carrito, llamamos al método que pintará la página.
				case "carrito":
					htmlCarrito();
				break;
				
				// En el caso de que el usuario haga clic sobre el juego y le de a 'Añadir a carrito'.
				case "articulo":
					
					// Comprobamos que la sesión no sea nula para pasar la sesión al carrito.
					if(hs.getAttribute("carrito") != null) listaCarrito = (ArrayList<String>) hs.getAttribute("carrito");
										
					// Establecemos un límite de elementos a añadir al carrito.
					// El usuario podrá comprar y reservar 15 artículos por cada compra.  Esto lo hago porque
					// cuando el usuario añade un elemento al carrito se descuenta automáticamente de la BD siendo
					// este artículo reservado para el usuario con un límite de 60 minutos sobre la sesión.
					// Si no pongo un máximo de elementos a añadir a la cesta, el usuario podría añadir tantos productos
					// como quisiera a la cesta y agotar la cantidad de los mismos sin que éstos no puedan ser reservados
					// o comprados por otros usuarios.
					if(listaCarrito.size() < 15){
					
						// Comprobamos que haya existencia sobre el artículo que se desea comprar o reservar.
						existencia = controlador.restaTabla(request.getParameter("id"));
						
						// Si existencia es true, querrá decir que habrá una cantidad superior a 0 sobre el artículo
						// en la BD.
						if(existencia){
							// Actualizamos el carrito y repintamos el html principal pasándole el total de artículos
							// que tiene la lista.
							actualizaCarrito(request.getParameter("id"));
							htmlPrincipal(listaCarrito.size());
						}
					
					}
					response.sendRedirect("marcogames?pagina=principal");
				break;
				
				// Si entramos al carrito y queremos eliminar un juego en concreto, éste volverá a la BD y se eliminará
				// del carrito.
				case "eliminar":
					try{
						eliminaCarrito(request.getParameter("indice"));
					}catch(Exception e){}
						response.sendRedirect("marcogames?pagina=carrito");
				break;
				
				// Este caso resolverá la compra final del usuario.  Para ello debemos estar en el carrito y que 
				// el mismo usuario haya pulsado sobre el botón comprar.
				case "comprar":
					
					// Comprobamos que la sesión no sea nula para verificar el posterior tamaño de la cesta.
					if(hs.getAttribute("carrito") != null){
						
						// Y rescatamos la lista de la sesión del usuario.
						listaCarrito = (ArrayList<String>) hs.getAttribute("carrito");
						
						// Si el tamaño del carrito es superior a 0, es decir, que haya 1 o más elementos, pintamos
						// el html de compra finalizada.
						if(listaCarrito.size() > 0){
							compraFinalizada();
							listaCarrito.clear();
						}else{
							// De lo contrario, querrá decir que no hay elementos en la cesta, y por lo tanto, el 
							// usuario no podrá seguir con la compra.
							sinElementosEnCesta();
						}
						
					}else{
						
						// Si el usuario entra por primera vez en la página sin haber introducido previamente un
						// artículo en cesta, la sesión es nula, luego querrá decir que en su cesta hay 0 artículos
						// y por lo tanto, pintaremos el html que corresponde al aviso de cesta vacía.
						sinElementosEnCesta();
					}
					
				break;
					
				// Cualquier enlace incorrecto que se le pase a la página nos llevará a otro html que pintará el error.
				default:
					htmlERROR();
			}
		}	
	}
	
	
	
	/**
	 * Este método eliminará el producto del carrito y sumará 1 a la tabla de la BD cuando el usuario elimine el producto
	 * de la cesta.
	 * @param indice
	 */
	private void eliminaCarrito(String indice) {
		// Capturamos la sesión y la pasamos a listaCarrito.  Posteriormente eliminamos el producto de la
		// lista y además cogemos el índice para pasarlo a la BD y hacer la suma del elemento sobre la misma.
		listaCarrito = (ArrayList<String>) hs.getAttribute("carrito");
		indice = listaCarrito.remove(Integer.parseInt(indice));
		controlador.sumaTabla(indice);
		hs.setAttribute("carrito", listaCarrito);		
	}

	
	
	/**
	 * Método que nos servirá para actualizar la cesta del carrito junto con la sesión.
	 * @param idArticulo
	 */
	private void actualizaCarrito(String idArticulo) {

		String id = idArticulo;
		
			// Si no obtenemos el id de la sesión previamente, limpiamos la lista del carrito y añadimos
			// el producto a la misma.  Posteriormente, creamos la sesión con la lista del carrito.
			if(hs.getAttribute("carrito") == null){
				listaCarrito.clear();
				listaCarrito.add(id);
				hs.setAttribute("carrito", listaCarrito);
			}else{
				// Si ya existe la sesión, añadimos la lista con el id del artículo a la sesión.
				listaCarrito = (ArrayList<String>) hs.getAttribute("carrito");
				listaCarrito.add(id);
				hs.setAttribute("carrito", listaCarrito);
			}
				
	}
	

	
	/**
	 * Pintamos el html principal de la web con todos los productos disponibles en tienda.
	 * Pintando así imagen, cantidad, precio, descripción...
	 * @param cantidad: Le pasamos este parámetro para pintar en el carrito la cantidad actual de la cesta.
	 */
	private void htmlPrincipal(int cantidad) {
		
		out.print("<html>"
				+ "<head>"
				+ "		<meta charset='UTF-8'>"
				+ "		<title> MARCOGAMES - PC | XBOX | PS4 - Español</title>"
				+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
				+ "</head>"
				+ "<body>"
				+ "		<div class='menu'> <a href='marcogames?pagina=carrito' title='Ir a mi Carrito'>  <img src='recursos/carrito.png' id='carrito' /> <span id='cantidad'> <b> &nbsp; "+cantidad+" &nbsp; </b> </span> </a></div>"
				+ "		<div class='cabecera pag_juegos'> <b>MARCO</b>GAMES </div>"
				+ "		<div class='contenedor_juegos'>"
				+ "<section>");
		
		Object [][] tabla = controlador.totalJuegos();
		
				for(int i = 0; i < tabla.length; i++){
					
					out.print("<article>"
							+ "		<div class='art'>"
										// Título del juego
							+ "			<h4>" + tabla[i][1] + "</h4>" 
										// Imagen del juego
							+ "			<img src='" + tabla[i][6] + "'>"
										// Precio del juego
							+ "			<p><b>"+ tabla[i][4] + " &euro;</b></p>"
							+ "			<p style='color:grey'><b>¡¡"+ tabla[i][5] + " uds. disponibles!!</b></p>"
										// Botón añadir producto a la cesta
							+ "			<a href='marcogames?pagina=articulo&id="+tabla[i][0]+"'> AÑADIR A CARRITO </a>"
							+ "			<div class='descripcion'>" 
											// Descripción del juego
							+ 				tabla[i][2]
							+ "			</div>"
							+ "		</div>"
							+ "</article>");

				}
				
		out.print("</section>"
				+ "	   </div>"
				+ "</body>"
				+ "</html>");	
	}
	
	
	
	/**
	 * Nos devuelve un error si la petición por parte del usuario es erronea.
	 */
	private void htmlERROR() {
		out.print("<html>"
				+ "<head>"
				+ "		<meta charset='UTF-8'>"
				+ "		<title> MARCOGAMES - PC | XBOX | PS4 </title>"
				+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
				+ "</head>"
				+ "<body>"
				+ "		<div class='cabecera'> <b>MARCO</b>GAMES </div>"
				+ "		<div class='contenedor'>"
				+ "			<h1> LA PÁGINA SOLICITADA NO EXISTE </h1>"
				+ "		</div>"
				+ "</body>"
				+ "</html>");
	}
	
	
	
	/**
	 * Pintamos la tabla con los productos añadidos a la cesta de la compra.
	 */
	private void htmlCarrito() {
		
		listaCarrito = (ArrayList<String>) hs.getAttribute("carrito");
			
		if(hs.getAttribute("carrito") != null){
			
			Object [][] tabla = controlador.listaJuegosCarrito(listaCarrito);
			int i;
			float total = 0;
			
			out.print("<html>"
					+ "<head>"
					+ "		<meta charset='UTF-8'>"
					+ "		<title> MARCOGAMES - Carrito </title>"
					+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
					+ "</head>"
					+ "<body>"
					+ "		<div class='cabecera'> <b>MARCO</b>GAMES </div>"
					+ "		<div class='contenedorCarrito'>"
					+ "		<table class ='tablaCarrito'>"
					+ "			<tr>"
					+ "				<th> JUEGO </th>"
					+ "				<th> PRECIO </th>"
					+ "				<th> ELIMINAR </th>");
			
					total = 0;
					
					for(i = 0; i < tabla.length; i++){
						
						total += Double.parseDouble((String) tabla[i][4]);
		
						   out.print( "			</tr>"
									+ "				<td> "+ tabla[i][1] +" </td>"
									+ "				<td> "+ tabla[i][4] +" &euro; </td>"
									+ "				<td class='eliminar'> <a href='marcogames?pagina=eliminar&indice="+i+"'> X </a></td>"
									+ "			<tr/>");
					}
	
		   out.print( "			<tr>"
					+ "				<th> TOTAL: </th>"
					+ "				<th colspan='2'> "+total+" &euro; </th>"
					+ "			<tr/>"
					+ "		<table>"
					+ "			<a href='marcogames?pagina=comprar'><h1 id='comprar'>¡COMPRAR!</h1></a>"
					+ "			<a href='marcogames?pagina=principal'><h2>Ir a Página Principal</h2></a>"
					+ "		</div>"
					+ "</body>"
					+ "</html>");
		   
			}else{
				
				out.print("<html>"
						+ "<head>"
						+ "		<meta charset='UTF-8'>"
						+ "		<title> MARCOGAMES - Carrito </title>"
						+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
						+ "</head>"
						+ "<body>"
						+ "		<div class='cabecera'> <b>MARCO</b>GAMES </div>"
						+ "		<div class='contenedorCarrito'>"
						+ "		<table class ='tablaCarrito'>"
						+ "			<tr>"
						+ "				<th> JUEGO </th>"
						+ "				<th> PRECIO </th>"
						+ "				<th> ELIMINAR </th>"
						+ "			<tr>"
						+ "				<th> TOTAL: </th>"
						+ "				<th colspan='2'> 0 &euro; </th>"
						+ "			<tr/>"
						+ "		<table>"
						+ "			<a href='marcogames?pagina=comprar'><h1 id='comprar'>¡COMPRAR!</h1></a>"
						+ "			<a href='marcogames?pagina=principal'><h2>Ir a Página Principal</h2></a>"
						+ "		</div>"
						+ "</body>"
						+ "</html>");
				
			}
	}


	
	/**
	 * Pintamos la página de bienvenida de la página web.
	 */
	private void htmlIndex() {
		out.print("<html>"
				+ "<head>"
				+ "		<meta charset='UTF-8'>"
				+ "		<title> MARCOGAMES - PC | XBOX | PS4 </title>"
				+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
				+ "</head>"
				+ "<body onload='alerta()'>"
				+ "			<div class='cabecera'><font size='16'> <b>Bienvenido a</b> </font> <br/><b>MARCO</b>GAMES </div>"
				+ "			<div class='contenedor'>"
				+ "				<a href='marcogames?pagina=principal'><h1>IR A MARCOGAMES</h1></a>"
				+ "			</div>"
				+ "	<script type='text/javascript' src='js/script.js'></script>"
				+ "</body>"
				+ "</html>");
	}
	
	
	
	/**
	 * Pantalla que nos saldrá si pulsamos sobre comprar y no hay ningún elemento en cesta.
	 */
	private void sinElementosEnCesta() {
		out.print("<html>"
				+ "<head>"
				+ "		<meta charset='UTF-8'>"
				+ "		<title> MARCOGAMES - PC | XBOX | PS4 - Español</title>"
				+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
				+ "</head>"
				+ "<body>"
				+ "		<div class='cabecera pag_juegos'> <b>MARCO</b>GAMES </div>"
				+ "		<br/>"
				+ "		<div class='contenedor'>"
				+ "			<h1> ¡SIN ELEMENTOS EN CESTA! </h1>"
				+ "			<a href='marcogames?pagina=principal'><h2>Ir a Página Principal</h2></a>"
				+ "		</div>"
				+ "</body>"
				+ "</html>");	
	}

	
	
	/**
	 * Cuando el usuario pulsa sobre el botón comprar, nos llevará a una pantalla de compra finalizada.
	 */
	private void compraFinalizada() {
		out.print("<html>"
				+ "<head>"
				+ "		<meta charset='UTF-8'>"
				+ "		<title> MARCOGAMES - PC | XBOX | PS4 - Español</title>"
				+ "		<link href='css/estilos.css' rel='stylesheet' type='text/css'"
				+ "</head>"
				+ "<body>"
				+ "		<div class='cabecera pag_juegos'> <b>MARCO</b>GAMES </div>"
				+ "		<br/>"
				+ "		<div class='contenedor'>"
				+ "			<h1> ¡COMPRA REALIZADA CON ÉXITO! </h1>"
				+ "			<a href='marcogames?pagina=principal'><h2>Volver a Página Principal</h2></a>"
				+ "		</div>"
				+ "</body>"
				+ "</html>");
	}

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
