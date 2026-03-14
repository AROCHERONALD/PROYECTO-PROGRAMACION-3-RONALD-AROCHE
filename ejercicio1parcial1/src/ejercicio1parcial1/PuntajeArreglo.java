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
