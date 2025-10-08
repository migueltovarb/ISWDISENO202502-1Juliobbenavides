import java.util.*;

class Dueno {
    private String nombre;
    private String documento;
    private String telefono;
    private List<Mascota> mascotas = new ArrayList<>();

    public Dueno(String nombre, String documento, String telefono) {
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public List<Mascota> getMascotas() { return mascotas; }

    public void agregarMascota(Mascota m) {
        for (Mascota mas : mascotas) {
            if (mas.getNombre().equalsIgnoreCase(m.getNombre())) {
                System.out.println("Error: Ya existe una mascota con ese nombre para este dueño.");
                return;
            }
        }
        mascotas.add(m);
    }
}

class Mascota {
    private String nombre;
    private String especie;
    private double edad;
    private Dueno dueno;
    private List<ControlVeterinario> controles = new ArrayList<>();

    public Mascota(String nombre, String especie, double edad, Dueno dueno) {
        this.nombre = nombre;
        this.especie = especie;
        this.edad = edad;
        this.dueno = dueno;
    }

    public String getNombre() { return nombre; }
    public Dueno getDueno() { return dueno; }

    public void agregarControl(ControlVeterinario c) {
        controles.add(c);
    }

    public void mostrarHistorial() {
        System.out.println("Historial de " + nombre + ":");
        if (controles.isEmpty()) {
            System.out.println("No hay controles registrados.");
        } else {
            for (ControlVeterinario c : controles) {
                System.out.println("- " + c.getFecha() + " | " + c.getTipo() + " | " + c.getObservaciones());
            }
        }
    }

    public void resumen() {
        System.out.println(nombre + " (" + especie + ") - Controles realizados: " + controles.size());
    }
}

class ControlVeterinario {
    private String fecha;
    private String tipo;
    private String observaciones;

    public ControlVeterinario(String fecha, String tipo, String observaciones) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.observaciones = observaciones;
    }

    public String getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public String getObservaciones() { return observaciones; }
}

public class SistemaVeterinario {
    private static List<Dueno> duenos = new ArrayList<>();

    public static Dueno buscarDueno(String documento) {
        for (Dueno d : duenos) {
            if (d.getDocumento().equalsIgnoreCase(documento)) return d;
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("\n Sistema Seguimiento Mascotas Veterinaria ");
            System.out.println("1. Registrar dueño");
            System.out.println("2. Registrar mascota");
            System.out.println("3. Registrar control veterinario");
            System.out.println("4. Consultar historial de mascota");
            System.out.println("5. Generar resumen de mascota");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Nombre dueño: ");
                    String nombre = sc.nextLine();
                    System.out.print("Documento: ");
                    String doc = sc.nextLine();
                    System.out.print("Teléfono: ");
                    String tel = sc.nextLine();

                    if (nombre.isEmpty() || doc.isEmpty() || tel.isEmpty()) {
                        System.out.println("Error: Campos vacíos no permitidos.");
                        break;
                    }

                    if (buscarDueno(doc) != null) {
                        System.out.println("Error: Ya existe un dueño con ese documento.");
                        break;
                    }

                    duenos.add(new Dueno(nombre, doc, tel));
                    System.out.println("Dueño registrado exitosamente.");
                    break;

                case 2:
                    System.out.print("Documento del dueño: ");
                    String docDueno = sc.nextLine();
                    Dueno d = buscarDueno(docDueno);
                    if (d == null) {
                        System.out.println("Error: Dueño no encontrado.");
                        break;
                    }
                    System.out.print("Nombre mascota: ");
                    String nomMasc = sc.nextLine();
                    System.out.print("Especie: ");
                    String especie = sc.nextLine();
                    System.out.print("Edad: ");
                    double edad = sc.nextDouble();
                    sc.nextLine();

                    if (nomMasc.isEmpty() || especie.isEmpty()) {
                        System.out.println("Error: Campos vacíos no permitidos.");
                        break;
                    }

                    Mascota m = new Mascota(nomMasc, especie, edad, d);
                    d.agregarMascota(m);
                    if (d.getMascotas().contains(m)) {
                        System.out.println("Mascota registrada exitosamente.");
                    }
                    break;

                case 3:
                    System.out.print("Documento del dueño: ");
                    String docD = sc.nextLine();
                    Dueno du = buscarDueno(docD);
                    if (du == null) {
                        System.out.println("Error: Dueño no encontrado.");
                        break;
                    }
                    System.out.print("Nombre de la mascota: ");
                    String nomM = sc.nextLine();
                    Mascota mascota = null;
                    for (Mascota ma : du.getMascotas()) {
                        if (ma.getNombre().equalsIgnoreCase(nomM)) {
                            mascota = ma;
                            break;
                        }
                    }
                    if (mascota == null) {
                        System.out.println("Error: Mascota no encontrada.");
                        break;
                    }
                    System.out.print("Fecha del control: ");
                    String fecha = sc.nextLine();
                    System.out.print("Tipo de control: ");
                    String tipo = sc.nextLine();
                    System.out.print("Observaciones: ");
                    String obs = sc.nextLine();

                    if (fecha.isEmpty() || tipo.isEmpty() || obs.isEmpty()) {
                        System.out.println("Error: Campos vacíos no permitidos.");
                        break;
                    }

                    mascota.agregarControl(new ControlVeterinario(fecha, tipo, obs));
                    System.out.println("Control registrado exitosamente.");
                    break;

                case 4:
                    System.out.print("Documento del dueño: ");
                    String docHist = sc.nextLine();
                    Dueno duH = buscarDueno(docHist);
                    if (duH == null) {
                        System.out.println("Error: Dueño no encontrado.");
                        break;
                    }
                    System.out.print("Nombre de la mascota: ");
                    String nomHist = sc.nextLine();
                    boolean encontrada = false;
                    for (Mascota ma : duH.getMascotas()) {
                        if (ma.getNombre().equalsIgnoreCase(nomHist)) {
                            ma.mostrarHistorial();
                            encontrada = true;
                            break;
                        }
                    }
                    if (!encontrada) {
                        System.out.println("Error: Mascota no encontrada.");
                    }
                    break;

                case 5:
                    System.out.print("Documento del dueño: ");
                    String docRes = sc.nextLine();
                    Dueno duR = buscarDueno(docRes);
                    if (duR == null) {
                        System.out.println("Error: Dueño no encontrado.");
                        break;
                    }
                    if (duR.getMascotas().isEmpty()) {
                        System.out.println("El dueño no tiene mascotas registradas.");
                    } else {
                        System.out.println("Resumen de mascotas de " + duR.getNombre() + ":");
                        for (Mascota ma : duR.getMascotas()) {
                            ma.resumen();
                        }
                    }
                    break;

                case 0:
                    System.out.println("Saliendo del sistema.");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
        sc.close();
    }
}