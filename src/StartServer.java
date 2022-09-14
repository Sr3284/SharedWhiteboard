import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class StartServer {

	public static void main(String[] args) {
		try {
			Server server = new ServerImpl();
			LocateRegistry.createRegistry(6600);
			Naming.rebind("LousaCollab", server);
			System.out.println("O server está pronto");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Ocorreu algum erro. Server fora do ar.");
		}
	}
}