public class Test {

    public int fib(int n){
        int a = 0, b = 1, c = 0, i;
        if (n < 2) return n;
        for (i = 1; i < n; i++)
        {
            c = a + b;
            a = b;
            b = c;
        }
        return c;
    }
}