public class Test {

    public int fib(int n){
        int a = 0, b = 1, c = 0, i;
        if (n < 2) return n;
        for (i = 1; i < n; i++)
        {
            c = a + b;
            if (a < b) continue;
            int j = 1;
            while (j < i) {
                int k = b - a;
                if (a < k) continue;
                int y = a + k;
                ++j;
            }
            int l = b - a;
        }
        return c;
    }
}