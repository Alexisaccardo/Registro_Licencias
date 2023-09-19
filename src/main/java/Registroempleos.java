import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Registroempleos {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("*****REGISTRO DE EMPLEADOS*****");

        boolean aux = true;

        while (aux) {

            System.out.println("1. Asignar empleado: ");
            System.out.println("2. Licencia Maternidad o Paternidad: ");
            System.out.println("3. Validar Licencias");
            System.out.println("4. Terminar");

            System.out.println("Ingrese un numero entre 1 - 4: ");
            int result = Integer.parseInt(scanner.nextLine());

            switch (result) {
                case 1:
                    System.out.println("Ingrese su edad: ");
                    int edad = Integer.parseInt(scanner.nextLine());

                    if (edad<18 || edad>60){
                        System.out.println("No tienes edad para hacer el registro");
                    }else{
                        System.out.println("Ingrese su Nit: ");
                        String nit = scanner.nextLine();

                        System.out.println("Ingrese su nombre: ");
                        String name = scanner.nextLine();

                        System.out.println("Ingrese su dirección de residencia: ");
                        String direction = scanner.nextLine();

                        Insert(nit, name, direction);
                    }
                    break;
                case 2:
                    System.out.println("Para continuar gestionando tu licencia de Maternidad o Paternidad por favor ingresar tu Nit");
                    String code = scanner.nextLine();

                    String licence = Select_One(code);

                    if (licence.equals("")){
                        System.out.println("No existe empleada con este nit "+ code);
                    }else {

                        System.out.println("Cuantos días solicitas para la licencia?: ");
                        int days = Integer.parseInt(scanner.nextLine());

                        Editar(code, days);
                    }

                    break;
                case 3:
                    System.out.println("Bienvenido al sistema de validación de Licencias");
                    System.out.println();
                    System.out.println("Ingresa el codigo del empleado a validar: ");
                    code = scanner.nextLine();

                    licence = Select_One(code);
                    if (licence.equals("")){
                        System.out.println("No existe empleada con este nit "+ code);
                    }else {
                        List<String> list_date = Select_Date(code);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate date_i = LocalDate.parse(list_date.get(0), formatter);
                        LocalDate date_f = LocalDate.parse(list_date.get(1), formatter);

                        long days = ChronoUnit.DAYS.between(date_i, date_f);

                        System.out.println(days);
                        if (days > 30){
                            System.out.println("SU LICENCIA ES MAYOR A 30 DIAS");

                            Editar_days(code);

                        }else {
                            System.out.println("Su licencia esta con normalidad");
                        }

                    }
                    break;
                case 4:
                    System.out.println("Finalizando...");
                    aux = false;
                    break;
                default:
                    System.out.println("Ingrese un numero valido");

            }
        }

    }

    private static void Editar_days(String code) throws ClassNotFoundException, SQLException {

        String driver2 = "com.mysql.cj.jdbc.Driver";
        String url2 = "jdbc:mysql://localhost:3306/empresa";
        String username2 = "root";
        String pass2 = "";

        Class.forName(driver2);
        Connection connection2 = DriverManager.getConnection(url2, username2, pass2);

        Statement statement2 = connection2.createStatement();

        String consulta = "UPDATE empleados SET fecha_inicio = ?, fecha_fin = ? WHERE nit = ?";
        PreparedStatement preparedStatement = connection2.prepareStatement(consulta);
        preparedStatement.setString(1, "");
        preparedStatement.setString(2, "");
        preparedStatement.setString(3, code);

        int filasActualizadas = preparedStatement.executeUpdate();
        if (filasActualizadas > 0) {
            System.out.println("Su licencia excede los días permitidos, ingrese nuevamente un rango entre 1 a 30 días");
        }

        preparedStatement.close();
        connection2.close();
    }

    private static void Insert(String nit, String name, String direction) {

        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/empresa";
        String username = "root";
        String password = "";

        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM empleados");


            // Sentencia INSERT
            String sql = "INSERT INTO empleados (nit, nombre, direccion, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?, ?)";

            // Preparar la sentencia
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nit);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, direction);
            preparedStatement.setString(4, "");
            preparedStatement.setString(5,"");

            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Funcionario registrado exitosamente.");
            } else {
                System.out.println("No se pudo registrar el funcionario.");
            }

            preparedStatement.close();
            connection.close();
            statement.close();
            resultSet.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void Editar(String code, int days) throws ClassNotFoundException, SQLException {
        String driver2 = "com.mysql.cj.jdbc.Driver";
        String url2 = "jdbc:mysql://localhost:3306/empresa";
        String username2 = "root";
        String pass2 = "";

        Class.forName(driver2);
        Connection connection2 = DriverManager.getConnection(url2, username2, pass2);

        Statement statement2 = connection2.createStatement();

        String consulta = "UPDATE empleados SET fecha_inicio = ?, fecha_fin = ? WHERE nit = ?";
        PreparedStatement preparedStatement = connection2.prepareStatement(consulta);
        preparedStatement.setString(1, LocalDate.now().toString());
        preparedStatement.setString(2, LocalDate.now().plusDays(days).toString());
        preparedStatement.setString(3, code);

        int filasActualizadas = preparedStatement.executeUpdate();
        if (filasActualizadas > 0) {
            System.out.println("Licencia Asignada con exito");
        } else {
            System.out.println("No se encontró el codigo de empleado para actualizar");
        }

        preparedStatement.close();
        connection2.close();
    }


    private static String Select_One(String code) throws ClassNotFoundException, SQLException {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/empresa";
        String username = "root";
        String password = "";

        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, username, password);

        String consultaSQL = "SELECT * FROM empleados WHERE nit = ?";

        PreparedStatement statement = connection.prepareStatement(consultaSQL);
        statement.setString(1, code);

        // Ejecutar la consulta
        ResultSet resultSet = statement.executeQuery();

        // Procesar el resultado si existe
        if (resultSet.next()) {
            String codigo = resultSet.getString("nit");
            String nombre = resultSet.getString("nombre");

            return nombre;

        } else {
            System.out.println("No se encontró un empleado registrado con el codigo ingresado.");
        }

        // Cerrar recursos
        resultSet.close();
        statement.close();
        connection.close();

        return "";
    }


    private static List<String> Select_Date(String code) throws ClassNotFoundException, SQLException {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/empresa";
        String username = "root";
        String password = "";

        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, username, password);

        String consultaSQL = "SELECT * FROM empleados WHERE nit = ?";

        PreparedStatement statement = connection.prepareStatement(consultaSQL);
        statement.setString(1, code);

        // Ejecutar la consulta
        ResultSet resultSet = statement.executeQuery();

        // Procesar el resultado si existe
        if (resultSet.next()) {
            String codigo = resultSet.getString("nit");
            String date_i = resultSet.getString("fecha_inicio");
            String date_f = resultSet.getString("fecha_fin");
            List<String> list_Date = new ArrayList<>();
            list_Date.add(date_i);
            list_Date.add(date_f);
            return list_Date;

        }

        // Cerrar recursos
        resultSet.close();
        statement.close();
        connection.close();

        return null;
    }
    }

