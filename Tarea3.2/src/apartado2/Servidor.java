package apartado2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author N
 *
 */
public class Servidor {

	public Servidor() {
		Socket socket = null;
		ServerSocket serverSocket = null;
		DataOutputStream dataOutputStream = null;
		DataInputStream dataInputStream = null;
		File ficheroEncontrado = null;
		
		try {
			serverSocket = new ServerSocket(1500);
			System.out.println("Servidor a la espera");
			socket = serverSocket.accept();
			dataOutputStream = new DataOutputStream(socket.getOutputStream());// para las salidas
			dataInputStream = new DataInputStream(socket.getInputStream());// para las entradas

			// busqueda del fichero dentro de la carpeta Servidor y envío
			ficheroEncontrado = buscarFichero(dataInputStream.readUTF());
			mandarDatosCliente(ficheroEncontrado, dataOutputStream);

		} catch (IOException e) {
			System.out.println("No se ha podido crear el serverSocket" + e.getMessage());
			System.exit(1);
		}

		cerrarConexion(serverSocket, socket, dataOutputStream, dataInputStream);

	}

	/*
	 * Este método busca el fichero en el directorio "servidor"
	 * 
	 */
	public File buscarFichero(String fichero) throws IOException {
		// No me hace falta saber el SO si accedo a su separador de directorios
		String separador = File.separator;

		// si es linux o windows diferente ruta C:\servidor\ o /servidor/
		String ruta = separador.equals("\\") ? "C:" : "";
		ruta += separador + "servidor" + separador;
		File dir = new File(ruta);

		if (dir.exists() && dir.isDirectory() && Arrays.asList(dir.list()).contains(fichero)) {
			// si contiene el fichero lo recojo y lo envío
			return new File(ruta + fichero);
		}
		return null;

	}

	/*
	 * Envío de datos al cliente
	 */
	public void mandarDatosCliente(File ficheroEncontrado, DataOutputStream dataOutputStream) {
		if (ficheroEncontrado != null) {// si se encontró se envía
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(ficheroEncontrado);
				BufferedReader br = new BufferedReader(fileReader);
				String linea = "";
				while ((linea = br.readLine()) != null) {
					dataOutputStream.writeUTF(linea);
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.out.println("Error al enviar el fichero al cliente" + e.getMessage());
			}
		} else {
			try {
				dataOutputStream.writeUTF("No se ha encontrado el fichero");
			} catch (IOException e) {
				System.out.println("Error al enviar el fichero al cliente" + e.getMessage());
			}
		}
	}

	/*
	 * Cierre de sockets y streams
	 */
	public void cerrarConexion(ServerSocket c, Socket s, DataOutputStream dataOutputStream,
			DataInputStream dataInputStream) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexión " + e.getMessage());
			}
		}
		if (s != null) {
			try {
				s.close();
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
		// creo un objeto de la clase Servidor
		new Servidor();

	}

}
