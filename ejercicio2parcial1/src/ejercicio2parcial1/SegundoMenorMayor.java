package ejercicio2parcial1;

public class SegundoMenorMayor {

    public static int[] secondMinMax(int[] numbers) {
        int menor = Integer.MAX_VALUE;
        int segundoMenor = Integer.MAX_VALUE;
        
        int mayor = Integer.MIN_VALUE;
        int segundoMayor = Integer.MIN_VALUE;

        for (int n : numbers) {

            // Buscar menor y segundo menor
            if (n < menor) {
                segundoMenor = menor;
                menor = n;
            } else if (n != menor && n < segundoMenor) {
                segundoMenor = n;
            }
            // Buscar mayor y segundo mayor
            if (n > mayor) {
                segundoMayor = mayor;
                mayor = n;
            } else if (n != mayor && n > segundoMayor) {
                segundoMayor = n;
            }
        }

        return new int[]{segundoMenor, segundoMayor};
    }

    public static void main(String[] args) {
        int[] arreglo = {100, 400, 1100, 600, 2200, 300};

        int[] resultado = secondMinMax(arreglo);

        System.out.println("Segundo menor: " + resultado[0]);
        System.out.println("Segundo mayor: " + resultado[1]);
    }
}


//“Lo que hice fue recorrer el arreglo una sola vez.
//Fui guardando el menor y el segundo menor, y también el mayor y el segundo mayor.
//Si encontraba un número más pequeño que el menor actual, entonces el menor pasaba a ser segundo menor.
//Y si encontraba uno más grande que el mayor actual, entonces el mayor pasaba a ser segundo mayor.
//Así no necesito ordenar el arreglo y cumplo con hacerlo en una sola pasada.”
