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
		//Defino sockets, streams y una variable File para el fichero solicitado:
		Socket socket = null;
		ServerSocket serverSocket = null;
		DataOutputStream dataOutputStream = null;
		DataInputStream dataInputStream = null;
		File ficheroEncontrado = null;
		
		try {
			serverSocket = new ServerSocket(1500);
			System.out.println("Servidor a la espera");
			
			socket = serverSocket.accept(); //cuando me llega una solicitud de conexi�n la acepto 
			
			dataOutputStream = new DataOutputStream(socket.getOutputStream());// stream para las salidas
			dataInputStream = new DataInputStream(socket.getInputStream());// stream para las entradas

			// busqueda del fichero dentro de la carpeta Servidor
			ficheroEncontrado = buscarFichero(dataInputStream.readUTF());
			//mandar los datos del fichero al cliente
			mandarDatosCliente(ficheroEncontrado, dataOutputStream);

		} catch (IOException e) {
			System.out.println("No se ha podido crear el serverSocket" + e.getMessage());
			System.exit(1);
		}
		//m�todo para cerrar sockets y streams:
		cerrarConexion(serverSocket, socket, dataOutputStream, dataInputStream);
		System.out.println("Env�o realizado : cerrando socket del servidor ... ");

	}

	/*
	 * Este m�todo busca el fichero en el directorio "servidor"
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
			// si contiene el fichero lo recojo y lo env�o
			return new File(ruta + fichero);
		}
		return null;

	}

	/*
	 * Env�o de datos al cliente
	 */
	public void mandarDatosCliente(File ficheroEncontrado, DataOutputStream dataOutputStream) {
		if (ficheroEncontrado != null) {// si se encontr� se env�a
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(ficheroEncontrado); 
				BufferedReader br = new BufferedReader(fileReader);
				
				String datosFile = "";
				
				//voy enviando los datos del fichero l�nea a l�nea
				while ((datosFile = br.readLine()) != null) {
					dataOutputStream.writeUTF(datosFile);
				}
				
				if (br != null) {//cierro el buffer de lectura
					br.close();
				}
			} catch (IOException e) {
				System.out.println("Error al enviar el fichero al cliente" + e.getMessage());
			}
		} else {//si no se encontr� el fichero se env�a un mensaje al cliente
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
		if (c != null) { //cierre socket servidor
			try {
				c.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
		if (s != null) { //cierre socket cliente
			try {
				s.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
		if (dataOutputStream != null) { //cierre stream salida
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
		if (dataInputStream != null) {//cierre stream entrada
			try {
				dataInputStream.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar la conexi�n " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		// creo un objeto de la clase Servidor
		new Servidor();

	}

}
