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
		Socket cliente = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		BufferedReader entrada = null;
		try {
			cliente = new Socket("localhost",1500);
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
	}

	/*
	 * Este m�todo solicita el nombre del fichero, hasta que se ingrese un valor
	 */
	public String pedirNombreFichero(BufferedReader entrada) {
		String fichero;
		while (true) {
			System.out.println("Introduzca nombre del fichero");
			try {
				fichero = entrada.readLine();
				if (fichero.isEmpty())
					continue;
				return fichero;
			} catch (IOException e) {
				System.out.println("Error al leer del teclado");
				System.exit(1);
			}
		}
	}

	/*
	 * Este m�todo env�a el nombre del fichero y muestra el contenido del fichero
	 * que recibe del servidor
	 */
	public void enviarYRecibirServidor(String fichero, DataOutputStream dataOutputStream,
			DataInputStream dataInputStream) {
		try {
			dataOutputStream.writeUTF(fichero);// enviar nombre fichero
		} catch (IOException e1) {
			System.out.println("Error al enviar nombre fichero" + e1.getMessage());
		}

		String linea = "";
		try {
			// mostrar datos de fichero
			while ((linea = dataInputStream.readUTF()) != null) {
				System.out.println(linea);
			}
		} catch (IOException e) {

		}
	}

	/*
	 * Con este m�todo cerramos buffers y el socket
	 */
	public void cerrarConexion(Socket c, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
		if (dataInputStream != null) {
			try {
				dataInputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		// creo un objeto de la clase cliente
		new Cliente();

	}

}
