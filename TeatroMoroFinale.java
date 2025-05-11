import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
@SuppressWarnings("all")

public class TeatroMoroFinale {

    // CONSTANTES
    private static final String NOMBRE_TEATRO = "Teatro Moro";
    private static final int CAPACIDAD_TOTAL = 100;
    private static final int[] PRECIOS = {30000, 18000, 15000, 13000}; // VIP, Platea Alta, Platea Baja, Palcos
    private static final String[] ZONAS = {"VIP", "Platea Alta", "Platea Baja", "Palcos"};
    private static final int NUM_ZONAS = ZONAS.length;
    private static final int TIEMPO_RESERVA_MINUTOS = 15; // Tiempo que dura la reserva

    // VARIABLES DE CONTROL
    private static int asientosDisponibles = CAPACIDAD_TOTAL;
    private static int entradasVendidas = 0;
    private static int ingresosTotales = 0;
    private static final List<String[]> ventas = new ArrayList<>(); // [nombre, rut, zona, cantidad, descuento, total]
    private static final List<String[]> reservas = new ArrayList<>(); // [nombre, rut, zona, cantidad, horaReserva, total]

    // ==============METODOS PRINCIPALES==================

    // Metodo main, es el que ejecuta el programa
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {

            System.out.println("\n=== " + NOMBRE_TEATRO + " ===");
            System.out.println("Asientos disponibles: " + asientosDisponibles);
            System.out.println("Reservas activas: " + reservas.size());
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Comprar entradas");
            System.out.println("2. Reservar entradas");
            System.out.println("3. Completar reserva");
            System.out.println("4. Ver última boleta");
            System.out.println("5. Resumen de ventas");
            System.out.println("0. Salir");
            System.out.print("\nSeleccione una opción: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1":
                    comprarEntradas(scanner);
                    break;
                case "2":
                    reservarEntradas(scanner);
                    break;
                case "3":
                    completarReserva(scanner);
                    break;
                case "4":
                    imprimirUltimaBoleta();
                    break;
                case "5":
                    resumenVentas();
                    break;
                case "0":
                    salir = true;
                    System.out.println("\n¡Gracias por usar el sistema!");
                    scanner.close();
                    break;
                default:
                    System.out.println("\nOpción inválida.");
            }
        }
    }


    // Metodo para comprar entradas
    private static void comprarEntradas(Scanner scanner) {
        if (asientosDisponibles == 0) {
            System.out.println("\n¡No hay asientos disponibles!");
            return;
        }

        // Selección de zona
        mostrarZonas();
        int zona = seleccionarZona(scanner);

        // Cantidad de entradas
        int cantidad = seleccionarCantidad(scanner);

        // Datos del cliente
        String nombre = obtenerNombre(scanner);
        String rut = validarRUT(scanner);

        // Descuentos
        double descuento = calcularDescuento(scanner);
        int total = (int) (PRECIOS[zona] * cantidad * (1 - descuento));

        // Confirmación
        System.out.println("\nResumen de compra:");
        System.out.println("Zona: " + ZONAS[zona]);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Descuento: " + (descuento * 100) + "%");
        System.out.println("Total a pagar: $" + total+"\n");

        while(true) {
            try {
                System.out.print("¿Confirmar compra? (Si/No): ");
                String opcion = scanner.nextLine();
                if (opcion.equalsIgnoreCase("Si")) {


                    // Registrar venta
                    String[] venta = {nombre, rut, ZONAS[zona], String.valueOf(cantidad),
                            String.valueOf(descuento), String.valueOf(total)};
                    ventas.add(venta);
                    entradasVendidas += cantidad;
                    ingresosTotales += total;
                    asientosDisponibles -= cantidad;

                    System.out.println("\n¡Compra exitosa!");
                    imprimirBoleta(venta);
                    break;
                } else if (opcion.equalsIgnoreCase("No")) {
                    System.out.println("Compra cancelada.");
                    break;
                } else {
                    System.out.println("Por favor ingrese una respuesta valida");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor ingrese una respuesta valida");
            }
        }
    }

    // Metodo para reservar entradas
    private static void reservarEntradas(Scanner scanner) {
        if (asientosDisponibles == 0) {
            System.out.println("¡No hay asientos disponibles para reservar!");
            return;
        }

        System.out.println("\n=== RESERVAR ENTRADAS ===");
        mostrarZonas();
        int zona = seleccionarZona(scanner);
        int cantidad = seleccionarCantidad(scanner);
        String nombre = obtenerNombre(scanner);
        String rut = validarRUT(scanner);

        // Registrar reserva
        reservas.add(new String[]{
                nombre,
                rut,
                ZONAS[zona],
                String.valueOf(cantidad),
        });

        asientosDisponibles -= cantidad;
        System.out.println("\n¡Reserva exitosa! Tienes " + TIEMPO_RESERVA_MINUTOS +
                " minutos para completar la compra.");
    }

    // Metodo para completar reserva
    private static void completarReserva(Scanner scanner) {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas pendientes.");
            return;
        }

        //Solicitamos rut
        String rut = validarRUT(scanner);
        List<Integer> reservasUsuario = new ArrayList<>();


        // Buscar reservas del usuario
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i)[1].equals(rut)) {
                reservasUsuario.add(i);
            }
        }

        if (reservasUsuario.isEmpty()) {
            System.out.println("No se encontraron reservas con ese RUT.");
            return;
        }

        // Mostrar reservas encontradas
        System.out.println("\nReservas encontradas:");
        for (int i = 0; i < reservasUsuario.size(); i++) {
            String[] reserva = reservas.get(reservasUsuario.get(i));
            System.out.println((i + 1) + ". " + reserva[0] + " - " + reserva[2] +
                    " (" + reserva[3] + " entradas)");
        }

        // Seleccionar reserva a completar

        int seleccion;
        while(true){
            System.out.print("Seleccione reserva a completar (1-" + reservasUsuario.size() + "): ");
            try {
                seleccion = Integer.parseInt(scanner.nextLine()) - 1;

                if (seleccion < 0 || seleccion >= reservasUsuario.size()) {
                    System.out.println("Por favor ingrese un número válido.");
                }else {
                    break;
                }
            }catch (NumberFormatException e){
                System.out.println("Por favor ingrese un número válido.");
            }
        }

        String[] reserva = reservas.get(reservasUsuario.get(seleccion));
        double descuento = calcularDescuento(scanner);
        int total = (int) (PRECIOS[obtenerIndiceZona(reserva[2])] *
                Integer.parseInt(reserva[3]) * (1 - descuento));


        // Confirmación
        System.out.println("\nResumen de compra:");
        System.out.println("Zona: " + reserva[2]);
        System.out.println("Cantidad: " + reserva[3]);
        System.out.println("Descuento: " + (descuento * 100) + "%");
        System.out.println("Total a pagar: $" + total+"\n");

        while(true){
            try {
                System.out.println("¿Confirmar compra? (Si/No): ");
                String opcion = scanner.nextLine();
                if (opcion.equalsIgnoreCase("Si")) {

                    String[] venta = {
                            reserva[0],
                            reserva[1],
                            reserva[2],
                            reserva[3],
                            String.valueOf(descuento),
                            String.valueOf(total)
                    };
                    // Registrar como venta
                    ventas.add(venta);


                    entradasVendidas += Integer.parseInt(reserva[3]);
                    ingresosTotales += total;

                    // Eliminar reserva
                    reservas.remove(reservasUsuario.get(seleccion).intValue());
                    System.out.println("¡Compra completada con éxito!");
                    imprimirBoleta(venta);
                    break;
                } else if (opcion.equalsIgnoreCase("No")) {
                    System.out.println("Compra cancelada. La reserva sigue activa.");
                    break;
                } else {
                    System.out.println("Por favor ingrese una respuesta valida");
                }
            }catch (InputMismatchException e){
                System.out.println("Por favor ingrese una respuesta valida");
            }
        }
    }


    // ==============METODOS AUXILIARES==================

    // Meatodo para validar RUT
    private static String validarRUT(Scanner scanner) {
        while (true) {
            System.out.print("RUT del cliente (sin guión ni puntos): ");
            String rut = scanner.nextLine().replaceAll("\\D", "");
            if (rut.length() >= 7 && rut.length() <= 9) {
                return rut;
            }
            System.out.println("RUT inválido. Intente nuevamente.");
        }
    }

    // Metodo para obtener el nombre del cliente
    private static String obtenerNombre(Scanner scanner) {
        // Expresión que permite letras mayúsculas y minúsculas, espacios y tildes
        final String REGEX_NOMBRE = "^[\\p{L} .'-]+$";

        while (true) {
            System.out.print("Nombre del cliente: ");
            String nombre = scanner.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                continue;
            }

            if (!nombre.matches(REGEX_NOMBRE)) {
                System.out.println("El nombre solo puede contener letras y espacios.");
                continue;
            }

            return nombre;
        }
    }

    // Metodo para seleccionar cantidad de entradas
    private static int seleccionarCantidad(Scanner scanner) {
        final int disponibles = asientosDisponibles;

        while (true) {
            System.out.print("Ingrese cantidad de entradas (1-" + disponibles + "): ");
            try {
                int cantidad = Integer.parseInt(scanner.nextLine());

                // Validaciones
                if (cantidad < 1) {
                    System.out.println("Debe comprar al menos 1 entrada.");
                    continue;
                }

                // Validación de capacidad máxima
                if (cantidad > CAPACIDAD_TOTAL) {
                    System.out.println("El teatro solo tiene capacidad para " + CAPACIDAD_TOTAL + " entradas en total.");
                    continue;
                }

                // Validación de disponibilidad actual
                if (cantidad > disponibles) {
                    System.out.println("Solo hay " + disponibles + " entradas disponibles.");
                    continue;
                }

                return cantidad;

            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    // Metodo para calcular descuento
    private static double calcularDescuento(Scanner scanner) {

        int genero;
        int edad;

        // Validar género
        while (true) {
            try {
                System.out.println("Ingrese su genero:\n1. Masculino\n2. Femenino");
                genero = Integer.parseInt(scanner.nextLine());
                if (genero == 1 || genero == 2) {
                    break;
                } else {
                    System.out.println("Por favor ingrese 1 o 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido (1 o 2).");
            }
        }

        // Validar edad
        while (true) {
            try {
                System.out.print("Edad del cliente: ");
                edad = Integer.parseInt(scanner.nextLine());
                if (edad >= 2 && edad <= 110) {
                    break;
                } else {
                    System.out.println("Edad inválida. Debe estar entre 2 y 110.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese una edad válida (número entero).");
            }
        }

        //  Calcular descuento

        /*
        Los descuentos de mujeres los comenzamos a aplicar únicamente desde los 18 años, ya que sería extraño a mi parecer que una niña de 5 años tenga
        un descuento de mujer y no por ser niño.
        Los de tercera edad se le aplica directamente sin ninguna otra validación debido a que es el mayor descuento aplicable.
         */

        if (edad >= 60){ // Tercera edad descuento de
            return 0.25;

        }else if (edad >=14){

            if (genero == 2 && edad >= 18){//Mujer mayor de 18 años se le aplica directamente el descuento de 20%
                return 0.20;
            }else {
                while (true) {
                    try {
                        System.out.println("¿Eres estudiante? (Si/No): ");
                        String respuesta = scanner.nextLine();
                        if (respuesta.equalsIgnoreCase("Si")) {//Estudiantes descuento de 15%
                            return 0.15;
                        } else if (respuesta.equalsIgnoreCase("No")) {
                            return 0;
                        } else {
                            System.out.println("Por favor ingrese una respuesta valida");
                        }

                    } catch (InputMismatchException e) {
                        System.out.println("Por favor ingrese una respuesta valido");
                    }
                }
            }

        }else return 0.10; //Niños tienen un descuento del 10% el rango de edad es de 2 a 13 años

    }

    // Metodo para seleccionar zona
    private static int seleccionarZona(Scanner scanner) {
        while (true) {
            System.out.print("Seleccione zona (1-" + NUM_ZONAS + "): ");
            try {
                int zona = Integer.parseInt(scanner.nextLine());
                if (zona < 1 || zona > NUM_ZONAS) {
                    System.out.println("Zona inválida. Debe estar entre 1 y " + NUM_ZONAS + ".");
                } else {
                    zona -= 1;
                    return zona; // Devolver índice de zona
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }


    }

    // Metodo para mostrar zonas disponibles
    private static void mostrarZonas() {
        System.out.println("\nZonas disponibles:");
        for (int i = 0; i < NUM_ZONAS; i++) {
            System.out.println((i + 1) + ". " + ZONAS[i] + " ($" + PRECIOS[i] + ")");
        }
    }

    // Metodo para obtener el índice de la zona
    public static int obtenerIndiceZona(String zonaBuscada) {
        for (int i = 0; i < ZONAS.length; i++) {
            if (ZONAS[i].equals(zonaBuscada)) { // Comparación exacta
                return i; // Retorna el índice si hay coincidencia
            }
        }
        return -1; // Retorna -1 si no se encuentra
    }

    // Metodo para imprimir boleta
    private static void imprimirBoleta(String[] venta) {
        System.out.println("\n=== BOLETA ===");
        System.out.println("Cliente: " + venta[0]);
        System.out.println("RUT: " + venta[1]);
        System.out.println("Zona: " + venta[2]);
        System.out.println("Cantidad: " + venta[3]);
        System.out.println("Descuento: " + (Double.parseDouble(venta[4]) * 100) + "%");
        System.out.println("Total: $" + venta[5]);
    }

    // Metodo para imprimir la última boleta
    private static void imprimirUltimaBoleta() {
        if (ventas.isEmpty()) {
            System.out.println("No hay boletas registradas.");
            return;
        }
        imprimirBoleta(ventas.getLast());
    }

    // Metodo para mostrar el resumen de ventas
    private static void resumenVentas() {
        System.out.print("\n=== RESUMEN DE VENTAS ===\n");
        System.out.println("Entradas vendidas: " + entradasVendidas);
        System.out.println("Ingresos totales: $" + ingresosTotales);
        System.out.println("\n=== DETALLE ===");
        System.out.printf("%-15s %-15s %-15s %-15s %-15s\n",
                "Cliente", "Zona", "Cantidad", "Descuento", "Total");
        for (String[] venta : ventas) {
            System.out.printf("%-15s %-15s %-15s %-15s %-15s\n",
                    venta[0], venta[2], venta[3], Double.parseDouble(venta[4])*100 + "%", "$" + venta[5]);
        }
    }

}
