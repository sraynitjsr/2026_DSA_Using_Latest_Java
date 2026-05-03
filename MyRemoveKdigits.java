import java.util.*;

public class MyRemoveKdigits {
    public String removeKdigits(String num, int k) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : num.toCharArray()) {
            while (!stack.isEmpty() && k > 0 && stack.peekLast() > c) {
                stack.pollLast();
                k--;
            }
            stack.addLast(c);
        }
        while (k > 0 && !stack.isEmpty()) {
            stack.pollLast();
            k--;
        }
        StringBuilder sb = new StringBuilder();
        boolean leadingZero = true;
        for (char c : stack) {
            if (leadingZero && c == '0') continue;
            leadingZero = false;
            sb.append(c);
        }
        if (sb.length() == 0) return "0";
        return sb.toString();
    }

    public static void RemoveKDigits(String[] args) {
        Scanner sc = new Scanner(System.in);
        String num = sc.next();
        int k = sc.nextInt();
        Solution sol = new Solution();
        System.out.println(sol.removeKdigits(num, k));
    }
}
