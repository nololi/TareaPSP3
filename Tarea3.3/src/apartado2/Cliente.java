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
			cliente = new Socket("localhost", 1500);
			dataOutputStream = new DataOutputStream(cliente.getOutputStream());// para mandar al servidor
			dataInputStream = new DataInputStream(cliente.getInputStream());// para lo que devuelve el servidor
			entrada = new BufferedReader(new InputStreamReader(System.in)); // para la entrada del datos del usuario
		} catch (IOException e) {
			System.out.println("No se ha podido crear el socket cliente" + e.getMessage());
			System.exit(1);
		}

		// pedir usuario
		String usuario = pedirDato(entrada, "usuario");
		// pedir contraseña
		String contra = pedirDato(entrada, "contraseña");

		// envío y recibo el directorio
		String serverResponse = enviarYRecibirServidor(usuario + "-" + contra, dataOutputStream, dataInputStream);
		if (serverResponse.contains("Error")) {
			System.out.println(serverResponse);
		} else {
			// Si el usuario y contraseña son correctos
			System.out.println(serverResponse);
			System.out.println("Introduzca nombre del fichero");
			// pedir nombre del fichero al usuario
			String fichero = pedirDato(entrada, "fichero");
			// enviar nombre del fichero al servidor y tratar la respuesta
			serverResponse = enviarYRecibirServidor(fichero, dataOutputStream, dataInputStream);

			System.out.println(serverResponse == null ? "Error, ha solicitado un directorio" : serverResponse);

		}
		// cerrar las conexiones, streams...
		cerrarConexion(cliente, dataOutputStream, dataInputStream);
	}

	/*
	 * Este método solicita el nombre del fichero, hasta que se ingrese un valor
	 */
	public String pedirDato(BufferedReader entrada, String valor) {
		String fichero;
		while (true) {
			System.out.println("Introduzca " + valor);
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
	 * Este método envía el nombre del fichero y muestra el contenido del fichero
	 * que recibe del servidor
	 */
	public String enviarYRecibirServidor(String fichero, DataOutputStream dataOutputStream,
			DataInputStream dataInputStream) {
		try {
			dataOutputStream.writeUTF(fichero);// enviar datos
		} catch (IOException e1) {
			System.out.println("Error al enviar al servidor" + e1.getMessage());
		}

		try {
			// recibir datos
			return dataInputStream.readUTF();
		} catch (IOException e) {
			System.out.println("Error al recibir del servidor" + (e.getMessage() == null ? "" : e.getMessage()));
		}
		return null;
	}

	/*
	 * Con este método cerramos buffers y el socket
	 */
	public void cerrarConexion(Socket c, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexión " + e.getMessage());
			}
		}
		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexión " + e.getMessage());
			}
		}
		if (dataInputStream != null) {
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
