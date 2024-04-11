import java.util.*;
import java.io.*;

public class Main {

    static class Position {
        int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "x : " + this.x + ", y : " + this.y;
        }
    }

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    static int L, N, Q;

    static int[] leftPower, damages;
    static int[][] map;

    static int[][] knightMap;
    static List<List<Position>> knights;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken()); // 체스판 크기
        N = Integer.parseInt(st.nextToken()); // 기사 수
        Q = Integer.parseInt(st.nextToken()); // 명령 수

        leftPower = new int[N + 1];
        damages = new int[N + 1];

        map = new int[L][L];
        for(int i = 0 ; i < L ; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0 ; j < L ; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 기사 정보 입력 (맵, 리스트)
        knightMap = new int[L][L];
        knights = new ArrayList<>();
        // init
        knights.add(new ArrayList<>());
        for(int n = 1 ; n <= N ; n++) {
            knights.add(new ArrayList<>());
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1; // 행 좌표
            int c = Integer.parseInt(st.nextToken()) - 1; // 열 좌표
            int h = Integer.parseInt(st.nextToken()); // 세로 길이
            int w = Integer.parseInt(st.nextToken()); // 가로 길이
            int k = Integer.parseInt(st.nextToken()); // 초기 체력

            // 초기 체력 입력
            leftPower[n] = k;
            
            // 지도에 기사 정보 표시
            for(int curR = r; curR < r + h ; curR++) {
                for(int curC = c ; curC < c + w ; curC++) {
                    knightMap[curR][curC] = n; // 맵에 저장
                    knights.get(n).add(new Position(curR, curC)); // 리스트에 저장
                }
            }
        }

        // 명령 수행
        for(int q = 0 ; q < Q ; q++) {
            st = new StringTokenizer(br.readLine());
            int i = Integer.parseInt(st.nextToken()); // 기사 번호
            int d = Integer.parseInt(st.nextToken()); // 명령
            
            if (test(i, d)) {
                push(i, d, true);
            }
        }

        // 생존한 기사들이 총 받은 데미지 합 출력
        int sum = 0;
        for(int i = 1 ; i <= N ; i++) {
            if (leftPower[i] > 0) {
                sum += damages[i];
            }
        }
        System.out.println(sum);
    }

    static void push(int index, int dir, boolean isFirst) {
        if (leftPower[index] == 0) {
            // 지도에서 사라진 기사 선택 시, 아무런 동작 하지 않음
            return;
        }

        Queue<Position> curPositions = new ArrayDeque<>(knights.get(index));
        Queue<Position> nextPositions = new ArrayDeque<>();

        // 모든 좌표 이동
        Set<Integer> nextIndexes = new HashSet<>();
        while(!curPositions.isEmpty()) {
            Position current = curPositions.poll();

            int nX = current.x + dx[dir];
            int nY = current.y + dy[dir];

            // 벽이 아님
            nextPositions.add(new Position(nX, nY));
            if (knightMap[nX][nY] != index && knightMap[nX][nY] != 0) {
                nextIndexes.add(knightMap[nX][nY]); // 밀리는 기사 번호 추가
            }
        }

        // 다음 기사 먼저 밀기
        for(int nextIndex : nextIndexes) {
            push(nextIndex, dir, false);
        }

        // 현재 레벨의 미는 작업 수행
        // 먼저 이전 위치 지우기
        for(Position prev : knights.get(index)) {
            knightMap[prev.x][prev.y] = 0;
        }

        // 이전 위치의 리스트 날리기
        knights.get(index).clear();
        
        // 새로운 위치 채우기
        int damageSum = 0;
        while(!nextPositions.isEmpty()) {
            Position next = nextPositions.poll();
            knightMap[next.x][next.y] = index;
            knights.get(index).add(next);

            // 채우면서 데미지 계산
            if (map[next.x][next.y] == 1) {
                damageSum += 1;
            }
        }

        if (isFirst) {
            // 명령을 받은 첫 기사라면 데미지 차감 X
            return;
        }

        if (damageSum < leftPower[index]) {
            // 데미지 초과 하지 않음
            leftPower[index] -= damageSum;
            damages[index] += damageSum;
            return;
        }

        // 데미지 초과함
        // 1. 지도에서 지우기
        for(Position cur : knights.get(index)) {
            knightMap[cur.x][cur.y] = 0;
        }
        // 2. 리스트 날리기
        knights.get(index).clear();
        // 3. leftPower 날리기
        leftPower[index] = 0;
    }

    static boolean test(int index, int dir) {
        if (leftPower[index] == 0) {
            // 지도에서 사라진 기사 선택 시, 아무런 동작 하지 않음
            return false;
        }

        Queue<Position> curPositions = new ArrayDeque<>(knights.get(index));

        // 모든 좌표 이동
        Set<Integer> nextIndexes = new HashSet<>();
        boolean available = true; // 밀려는 곳에 벽이 있다면 불가능
        while(!curPositions.isEmpty()) {
            Position current = curPositions.poll();

            int nX = current.x + dx[dir];
            int nY = current.y + dy[dir];

            if (nX < 0 || nX >= L || nY < 0 || nY >= L || map[nX][nY] == 2) {
                available = false;
                break;
            }

            // 벽이 아님
            if (knightMap[nX][nY] != index && knightMap[nX][nY] != 0) {
                nextIndexes.add(knightMap[nX][nY]); // 밀리는 기사 번호 추가
            }
        }

        if (!available) {
            // 미는게 불가능하다면, 밀지 않고 반환
            return false;
        }

        // 다음 기사 밀어보기
        for(int nextIndex : nextIndexes) {
            if (!test(nextIndex, dir)) {
                return false;
            }
        }

        return true;
    }
}