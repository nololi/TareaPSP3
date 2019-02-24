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
import java.util.List;

/**
 * @author N
 *
 */
public class Servidor extends Thread {
	Socket socket;

	private static final int PUERTO = 1500;
	private static int NUMERO_CLIENTE = 1;
	private static final File DIRECTORY = new File(System.getProperty("user.dir"));
	private static final String[] LISTADO_FICHEROS_TEXTO = DIRECTORY.list();
	private static final File[] LISTADO_TIPO_FICHERO = DIRECTORY.listFiles();
	private final String usuario = "Noelia";
	private final String contra = "abc123";
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

		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());// para las salidas
			dataInputStream = new DataInputStream(socket.getInputStream());// para las entradas

			String data = dataInputStream.readUTF();
			String nombre = data.split("-")[0];
			String password = data.split("-")[1];
			if (!nombre.equals(usuario) && !password.equals(contra)) {
				mandarDatosCliente(dataOutputStream, "Error : Usuario y/o contraseña errónea");
				System.out.println("Desconectado ");
			} else {
				// muestro directorio actual y envío los nombres del contenido
				String directoryNameFiles = "";
				String line = "";
				for (int i = 0; i < LISTADO_FICHEROS_TEXTO.length; i++) {
					String tipoFichero = LISTADO_TIPO_FICHERO[i].isDirectory() ? "Directorio :" : "Fichero :";
					directoryNameFiles += line + tipoFichero + LISTADO_FICHEROS_TEXTO[i];
					line = "\n";
				}

				mandarDatosCliente(dataOutputStream, directoryNameFiles);
				// quedo a la espera de que me pida qué fichero mostrar
				String respuesta = dataInputStream.readUTF();

				// si el fichero que ha introducido no existe error
				if (!directoryNameFiles.contains(respuesta)) {
					mandarDatosCliente(dataOutputStream, "El fichero solicitado no existe");
				} else {
					File ficheroEncontrado = buscarFichero(respuesta);
					buscarYMandarFichero(ficheroEncontrado, dataOutputStream);
				}
			}

		} catch (IOException e) {
			System.out.println("No se ha podido crear el serverSocket" + e.getMessage());
			System.exit(1);
		}

		cerrarConexion(socket, dataOutputStream, dataInputStream);
		System.out.println("Cliente " + getMiNumeroCliente() + " desconectado");
	}

	/*
	 * 
	 */
	public void buscarYMandarFichero(File ficheroEncontrado, DataOutputStream dataOutputStream) {
		if (ficheroEncontrado != null) {// si se encontró se envía
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(ficheroEncontrado);
				BufferedReader br = new BufferedReader(fileReader);
				String lineaFichero = "";
				String datos = "";
				String saltoLinea = "";
				while ((lineaFichero = br.readLine()) != null) {
					datos += saltoLinea + lineaFichero;
					saltoLinea = "\n";
				}
				dataOutputStream.writeUTF(datos);
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
	 * Envío de datos al cliente
	 */
	public void mandarDatosCliente(DataOutputStream dataOutputStream, String data) {
		try {
			dataOutputStream.writeUTF(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
