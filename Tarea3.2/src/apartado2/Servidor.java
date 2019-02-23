package apartado2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author N
 *
 */
public class Servidor extends Thread {
	Socket socket;

	private static final int PUERTO = 1500;
	private static int NUMERO_CLIENTE = 1;
	private int miNumeroCliente;

	public Servidor(Socket sCliente) {
		socket = sCliente;
	}

	public int getMiNumeroCliente() {
		return miNumeroCliente;
	}

	public void setMiNumeroCliente(int miNumeroCliente) {
		this.miNumeroCliente = miNumeroCliente;
	}

	public void run() {
		System.out.println("Conexión creada correctamente número " + NUMERO_CLIENTE);
		setMiNumeroCliente(NUMERO_CLIENTE);
		NUMERO_CLIENTE++;
		DataInputStream dataInputStream = null;
		try {
			dataInputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		DataOutputStream dataOutputStream = null;
		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		File ficheroEncontrado = null;
		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());// para las salidas
			dataInputStream = new DataInputStream(socket.getInputStream());// para las entradas

			// busqueda del fichero dentro de la carpeta Servidor y envío
			ficheroEncontrado = buscarFichero(dataInputStream.readUTF());
			mandarDatosCliente(ficheroEncontrado, dataOutputStream);

		} catch (IOException e) {
			System.out.println("No se ha podido crear el serverSocket" + e.getMessage());
			System.exit(1);
		}
		
		cerrarConexion(socket, dataOutputStream, dataInputStream);
		System.out.println("Cliente " + getMiNumeroCliente() +" desconectado");
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
	public void cerrarConexion(Socket s, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
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
		Socket socket = null;

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PUERTO); // servidor en escucha puerto 2000
			System.out.println("Servidor a la espera");
			while (true) {
				socket = serverSocket.accept();// acepto la conexión con el cliente
				new Servidor(socket).start();
			}

		} catch (IOException e) {
			System.out.println("Error al conectarse" + e.getMessage());
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
