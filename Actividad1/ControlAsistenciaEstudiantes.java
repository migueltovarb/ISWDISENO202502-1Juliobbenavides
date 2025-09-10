import java.util.Scanner;

public class ControlAsistenciaEstudiantes {
    static final int DIAS_SEMANA = 5;
    static final int NUMERO_ESTUDIANTES = 4;
    static final String[] ESTUDIANTES = {"Luis", "Maria", "Felipe", "Julian"};
    static final String[] DIAS = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes"};

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        char[][] asistencia = new char[NUMERO_ESTUDIANTES][DIAS_SEMANA];

        registrarAsistencia(sc, asistencia);

        int opcion;
        do {
            System.out.println("\n1. Ver asistencia individual");
            System.out.println("2. Ver resumen general");
            System.out.println("3. Volver a registrar");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1: verAsistenciaIndividual(sc, asistencia); break;
                case 2: mostrarResumen(asistencia); break;
                case 3: registrarAsistencia(sc, asistencia); break;
                case 4: System.out.println("¡Hasta luego!"); break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 4);
    }

    static void registrarAsistencia(Scanner sc, char[][] asistencia) {
        for (int i = 0; i < NUMERO_ESTUDIANTES; i++) {
            System.out.println(ESTUDIANTES[i] + ":");
            for (int j = 0; j < DIAS_SEMANA; j++) {
                char valor;
                do {
                    System.out.print(DIAS[j] + " (P/A): ");
                    valor = sc.next().toUpperCase().charAt(0);
                } while (valor != 'P' && valor != 'A');
                asistencia[i][j] = valor;
            }
        }
    }

    static void verAsistenciaIndividual(Scanner sc, char[][] asistencia) {
        for (int i = 0; i < NUMERO_ESTUDIANTES; i++) {
            System.out.println((i + 1) + ". " + ESTUDIANTES[i]);
        }
        System.out.print("Seleccione estudiante (1-" + NUMERO_ESTUDIANTES + "): ");
        int idx = sc.nextInt() - 1;

        if (idx < 0 || idx >= NUMERO_ESTUDIANTES) {
            System.out.println("Estudiante no válido.");
            return;
        }

        System.out.println("Asistencia de " + ESTUDIANTES[idx] + ":");
        for (int j = 0; j < DIAS_SEMANA; j++) {
            System.out.println(DIAS[j] + ": " + (asistencia[idx][j] == 'P' ? "Presente" : "Ausente"));
        }
    }

    static void mostrarResumen(char[][] asistencia) {
        for (int i = 0; i < NUMERO_ESTUDIANTES; i++) {
            int presentes = 0;
            for (int j = 0; j < DIAS_SEMANA; j++) {
                if (asistencia[i][j] == 'P') presentes++;
            }
            System.out.println(ESTUDIANTES[i] + ": " + presentes + " asistencias");
        }

        System.out.print("\nEstudiantes que asistieron todos los días: ");
        boolean hayPerfectos = false;
        for (int i = 0; i < NUMERO_ESTUDIANTES; i++) {
            boolean perfecto = true;
            for (int j = 0; j < DIAS_SEMANA; j++) {
                if (asistencia[i][j] != 'P') perfecto = false;
            }
            if (perfecto) {
                System.out.print(ESTUDIANTES[i] + " ");
                hayPerfectos = true;
            }
        }
        if (!hayPerfectos) System.out.print("Ninguno");

        System.out.print("\nDías con mayor número de ausencias: ");
        int maxAusencias = 0;
        for (int j = 0; j < DIAS_SEMANA; j++) {
            int ausencias = 0;
            for (int i = 0; i < NUMERO_ESTUDIANTES; i++) {
                if (asistencia[i][j] == 'A') ausencias++;
            }
            if (ausencias > maxAusencias) maxAusencias = ausencias;
        }

        boolean hayAusencias = false;
        for (int j = 0; j < DIAS_SEMANA; j++) {
            int ausencias = 0;
            for (int i = 0; i < NUMERO_ESTUDIANTES; i++) {
                if (asistencia[i][j] == 'A') ausencias++;
            }
            if (ausencias == maxAusencias && maxAusencias > 0) {
                System.out.print(DIAS[j] + "(" + maxAusencias + ") ");
                hayAusencias = true;
            }
        }
        if (!hayAusencias) System.out.print("Ninguno");
        System.out.println();
    }
}