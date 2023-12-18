import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[][] board = new int[n][n];
        for(int i = 0 ; i < n ; i++) {
            for(int j = 0 ; j < n ; j++) {
                board[i][j] = sc.nextInt();
            }
        }
        sc.close();

        // 마지막 위치가 [i][j]일때의 누적합의 최대값
        int[][] dp = new int[n][n];

        // 초기값 채우기
        dp[0][0] = board[0][0];
        for(int i = 1 ; i < n ; i++) {
            dp[0][i] = dp[0][i - 1] + board[0][i];
        }
        for(int i = 1 ; i < n ; i++) {
            dp[i][0] = dp[i - 1][0] + board[i][0];
        }

        for(int i = 1 ; i < n ; i++) {
            for(int j = 1 ; j < n ; j++) {
                // 위 또는 왼쪽에서의 값을 가져와 비교
                dp[i][j] = board[i][j] + Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        System.out.println(dp[n - 1][n - 1]);
    }
}