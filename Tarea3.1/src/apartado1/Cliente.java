package apartado1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * @author N
 *
 */
public class Cliente {

	public static void main(String[] args) {

		Socket clientSocket = null;
		DataOutputStream dataOutputStream = null;
		DataInputStream dataInputStream = null;
		BufferedReader entrada = null;
		int valorEntradaCliente;

		try {
			clientSocket = new Socket("localhost", 2000);// me conecto al servidor
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());// para mandar al servidor
			dataInputStream = new DataInputStream(clientSocket.getInputStream());// para lo que devuelve el servidor

			while (true) {
				System.out.println("Introduzca un número del 0  al 100");
				entrada = new BufferedReader(new InputStreamReader(System.in));
				valorEntradaCliente = Integer.parseInt(entrada.readLine());
				System.out.println("ha introducido " + valorEntradaCliente);
				dataOutputStream.writeInt(valorEntradaCliente);
				System.out.println(dataInputStream.readUTF());// imprimo lo que me devuelve el servidor
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
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

	}

}
