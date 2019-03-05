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

	private static final int PUERTO = 2000;// puerto utilizado
	private static int NUMERO_CLIENTE = 1; // contador global de clientes
	private static final File DIRECTORY = new File(System.getProperty("user.dir"));// directorio actual
	private static final String[] LISTADO_NOMBRES_FICHERO = DIRECTORY.list();// listado nombre ficheros (String)
	private static final File[] LISTADO_TIPO_FICHERO = DIRECTORY.listFiles();// listado ficheros como objetos File

	private final String usuario = "Noelia";// usuario para el login
	private final String contra = "abc123"; // contraseña para el login
	private int miNumeroCliente = 1;// número del cliente que se ha conectado

	public Servidor(Socket sCliente) {// constructor que inicializa el servidor con el socketcliente
		socket = sCliente;
	}

	public int getMiNumeroCliente() {
		return miNumeroCliente;
	}

	public void setMiNumeroCliente(int miNumeroCliente) {
		this.miNumeroCliente = miNumeroCliente;
	}

	public void run() {
		// muestro mensaje, asigno número de cliente, y lo aumento en +1
		System.out.println("Conexión creada correctamente número " + NUMERO_CLIENTE);
		setMiNumeroCliente(NUMERO_CLIENTE);
		NUMERO_CLIENTE++;

		DataInputStream dataInputStream = null;// Stream de entrada
		DataOutputStream dataOutputStream = null;// Stream de salida

		try {
			// inicializo streams de entrada y de salida
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());

			// espero a que me manden el usuario y contraseña correcto antes de continuar,
			while (true) {
				String data = dataInputStream.readUTF();
				String nombre = data.split("-")[0];
				String password = data.split("-")[1];
				boolean loginOK = nombre.equals(usuario) && password.equals(contra);
				if (!loginOK) {
					mandarDatosCliente(dataOutputStream, "Error : Usuario y/o contraseña errónea");
				} else {
					break; // contraseña ok :salir
				}
			}

			// recojo directorio actual por tipo (otro directorio/fichero) y nombre y lo
			// almaceno en directoryNameFiles
			String directoryNameFiles = "";
			String line = "";
			for (int i = 0; i < LISTADO_NOMBRES_FICHERO.length; i++) {
				String tipoFichero = LISTADO_TIPO_FICHERO[i].isDirectory() ? "Directorio :" : "Fichero :";
				directoryNameFiles += line + tipoFichero + LISTADO_NOMBRES_FICHERO[i];
				line = "\n";
			}

			// mando el contenido del directorio y quedo a la espera de que me pida qué
			// fichero mostrar
			mandarDatosCliente(dataOutputStream, directoryNameFiles);
			String ficheroSolicitado = dataInputStream.readUTF(); // fichero solicitado

			if (!directoryNameFiles.contains(ficheroSolicitado)) {// si el fichero que ha introducido no existe error
				mandarDatosCliente(dataOutputStream, "El fichero solicitado no existe");
			} else { // si existe busco fichero y lo mando
				File ficheroEncontrado = buscarFichero(ficheroSolicitado);
				buscarYMandarFichero(ficheroEncontrado, dataOutputStream);
			}

		} catch (IOException e) {
			System.out.println("No se ha podido crear el serverSocket" + e.getMessage());
			System.exit(1);
		} finally {
			// cerramos conexiones al finalizar
			cerrarConexion(socket, dataOutputStream, dataInputStream);
			System.out.println("Cliente " + getMiNumeroCliente() + " desconectado");
		}

	}

	/*
	 * método que busca el fichero solicitado por el cliente y envía su contenido
	 */
	public void buscarYMandarFichero(File ficheroEncontrado, DataOutputStream dataOutputStream) {
		if (ficheroEncontrado != null) {// si se encontró un fichero con ese nombre se envía
			FileReader fileReader = null;
			BufferedReader br = null;
			try {
				fileReader = new FileReader(ficheroEncontrado);
				br = new BufferedReader(fileReader);
				String lineaFichero = ""; // cada línea leída del fichero
				String datosParaCliente = ""; // datos que voy a mandar
				String saltoLinea = "";
				// almaceno el contenido del fichero en el String datosParaCliente
				while ((lineaFichero = br.readLine()) != null) {
					datosParaCliente += saltoLinea + lineaFichero;
					saltoLinea = "\n";
				}
				// envío los datos al cliente
				mandarDatosCliente(dataOutputStream, datosParaCliente);

			} catch (IOException e) {
				System.out.println("Error al enviar el fichero al cliente" + e.getMessage());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println("Error al cerrar el bufferedReader");
					}
				}
			}
		} else {
			try {// si el fichero no se ha encontrado
				dataOutputStream.writeUTF("No se ha encontrado el fichero");
			} catch (IOException e) {
				System.out.println("Error al contactar al cliente" + e.getMessage());
			}
		}
	}

	/*
	 * Este método busca el fichero en el directorio "servidor"
	 * 
	 */
	public File buscarFichero(String fichero) throws IOException {
		if (Arrays.asList(DIRECTORY.list()).contains(fichero)) {
			// si el directorio contiene el fichero lo recojo y lo envío
			return new File(DIRECTORY + File.separator + fichero);
		}
		return null;

	}

	/*
	 * Función genérica de envío de datos al cliente
	 */
	public void mandarDatosCliente(DataOutputStream dataOutputStream, String data) {
		try {
			dataOutputStream.writeUTF(data);
		} catch (IOException e) {
			System.out.println("Error al contactar al cliente" + e.getMessage());
			e.printStackTrace();
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
			// cerramos los sockets
			System.out.println("Finally");
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
