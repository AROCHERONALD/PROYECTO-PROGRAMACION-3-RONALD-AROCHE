public class PuntajeArreglo {

    public static int score(int[] numbers) {
        int total = 0;

        for (int number : numbers) {
            if (number == 5) {
                total += 5;
            } else if (number % 2 == 0) {
                total += 1;
            } else {
                total -= 3;
            }
        }

        return total;
    }

    public static void main(String[] args) {
        int[] arreglo1 = {1, 2, 3, 4, 5};
        int[] arreglo2 = {17, 19, 21};
        int[] arreglo3 = {5, 5, 5};

        System.out.println("Resultado 1: " + score(arreglo1));
        System.out.println("Resultado 2: " + score(arreglo2));
        System.out.println("Resultado 3: " + score(arreglo3));
    }
}