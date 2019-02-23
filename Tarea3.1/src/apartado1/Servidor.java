package apartado1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author N
 *
 */
public class Servidor extends Thread {
	Socket socket;

	static final int Puerto = 2000;
	private static int numeroCliente=1;
	private int miNumeroCliente;
	private int numeroAAcertar;

	public Servidor(Socket sCliente) {
		numeroAAcertar = (int) (Math.random() * 100);
		socket = sCliente;
	}
	
	
	
	public int getMiNumeroCliente() {
		return miNumeroCliente;
	}



	public void setMiNumeroCliente(int miNumeroCliente) {
		this.miNumeroCliente = miNumeroCliente;
	}



	public void mostrarNumero() {
		System.out.println("El número que tiene que acertar el cliente " + numeroCliente +" es " + numeroAAcertar);
	}

	public void run() {
		mostrarNumero();
		// Creo los flujos de entrada y salida
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

		int numeroRecibido;	
		
		try {		
			// ATENDER PETICIÓN DEL CLIENTE
			System.out.println("Conexión creada correctamente número " + numeroCliente);
			setMiNumeroCliente(numeroCliente);
			numeroCliente++;

			while (true) {
				numeroRecibido = dataInputStream.readInt(); // leo el número que me ha pasado el cliente
				if (numeroRecibido == numeroAAcertar) {
					dataOutputStream.writeUTF("Felicidades, ha acertado el número, \nCerrando conexiones....");
					break;
				} else {
					dataOutputStream
							.writeUTF(numeroRecibido < numeroAAcertar ? "El número es mayor " : " El número es menor");
				}

			}
			// Se cierra la conexión
			socket.close();
			System.out.println("Cliente " + getMiNumeroCliente() +" desconectado");
		} catch (Exception e) {
			System.out.println(e.getMessage());

		} finally {
			try {
				dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {

		Socket socket = null;

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Puerto); // servidor en escucha puerto 2000

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
