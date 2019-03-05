package apartado2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author N
 *
 */
public class Cliente {

	public Cliente() {
		//defino socket, streams y buffer que voy a utilizar
		Socket cliente = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		BufferedReader entrada = null;
		
		try {
			cliente = new Socket("localhost", 1500);//socket cliente 
			dataOutputStream = new DataOutputStream(cliente.getOutputStream());// para mandar al servidor
			dataInputStream = new DataInputStream(cliente.getInputStream());// para lo que devuelve el servidor
			entrada = new BufferedReader(new InputStreamReader(System.in)); // para la entrada del datos del usuario
		} catch (IOException e) {
			System.out.println("No se ha podido crear el socket cliente" + e.getMessage());
			System.exit(1);
		}

		// pedir nombre del fichero al usuario
		String fichero = pedirNombreFichero(entrada);
		// enviar nombre del fichero al servidor y tratar la respuesta
		enviarYRecibirServidor(fichero, dataOutputStream, dataInputStream);

		// cerrar las conexiones, streams...
		cerrarConexion(cliente, dataOutputStream, dataInputStream);
		
		System.out.println("Finalizando: cerrando socket del cliente ... ");
	}

	/*
	 * Este método solicita el nombre del fichero, hasta que se ingrese un valor
	 */
	public String pedirNombreFichero(BufferedReader entrada) {
		String fichero;
		while (true) { 
			System.out.println("Introduzca nombre del fichero");
			try {
				fichero = entrada.readLine(); //leo el valor introducido por cliente
				if (fichero.isEmpty())//si no ha introducido nada vuelvo a pedir el nombre fichero
					continue;
				return fichero;
			} catch (IOException e) {
				System.out.println("Error al leer del teclado");
				System.exit(1);
			}
		}
	}

	/*
	 * Este método envía el nombre del fichero y muestra el contenido del fichero
	 * que recibe del servidor
	 */
	public void enviarYRecibirServidor(String fichero, DataOutputStream dataOutputStream,
			DataInputStream dataInputStream) {
		try {
			dataOutputStream.writeUTF(fichero);// enviar nombre fichero al Servidor
		} catch (IOException e1) {
			System.out.println("Error al enviar nombre fichero" + e1.getMessage());
		}

		String linea = ""; //para ir recorriendo el stream recibido y mostrando al cliente
		try {
			// mostrar datos recibidos del servidor según los voy recibiendo
			while ((linea = dataInputStream.readUTF()) != null) {
				System.out.println(linea);
			}
		} catch (IOException e) {

		}
	}

	/*
	 * Con este método cerramos buffers y el socket
	 */
	public void cerrarConexion(Socket c, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
		if (c != null) { //cierro socket si no es null
			try {
				c.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexión " + e.getMessage());
			}
		}
		if (dataOutputStream != null) { //cierro stream salida si no es null
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexión " + e.getMessage());
			}
		}
		if (dataInputStream != null) { //cierro stream entrada si no es null
			try {
				dataInputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexión " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		// creo un objeto de la clase cliente
		new Cliente();

	}

}
