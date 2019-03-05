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
public class Servidor {

	public static void main(String[] args) {

		Socket socket = null;//socket cliente
		int numeroRecibido; //numero enviado por el cliente
		int numeroAAcertar = (int) (Math.random() * 100); //número que tiene que acertar el cliente
		DataOutputStream dataOutputStream = null; //stream salida
		DataInputStream dataInputStream = null;//stream entrada
		
		System.out.println("El número que tiene que acertar es " + numeroAAcertar); //control : para comprobar

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(2000); // servidor en escucha puerto 2000

			socket = serverSocket.accept();// acepto la conexión con el cliente
			System.out.println("Conexión creada correctamente");
			
			dataOutputStream = new DataOutputStream(socket.getOutputStream());// para las salidas
			dataInputStream = new DataInputStream(socket.getInputStream());// para las entradas

			while (true) {
				numeroRecibido = dataInputStream.readInt(); // leo el número que me ha pasado el cliente
				if (numeroRecibido == numeroAAcertar) {
					dataOutputStream.writeUTF("Felicidades, ¡ha acertado el número! \nCerrando conexiones....");
					break;
				} else {
					dataOutputStream
							.writeUTF(numeroRecibido < numeroAAcertar ? "El número es mayor " : "El número es menor");
				}

			}		

		} catch (IOException e) {
			System.out.println("Error al conectarse");
		} finally {//cerrando sockets y streams
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		
		System.out.println("Juego finalizado");

	}

}
