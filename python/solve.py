import sys

n, m = map(int, sys.stdin.readline().split())
row = []
for _ in range(n):
    data = list(map(int, sys.stdin.readline().split()))
    if not data or data == [0]:
        row.append([])
    else:
        row.append(data)

col = []
for _ in range(m):
    data = list(map(int, sys.stdin.readline().split()))
    if not data or data == [0]:
        col.append([])
    else:
        col.append(data)

board = [[0] * m for _ in range(n)]

# row와 col의 유효성 검사
for r in row:
    if sum(r) + len(r) - 1 > m:
        print('Invalid Row Input')
        exit()

for c in col:
    if sum(c) + len(c) - 1 > n:
        print('Invalid Column Input')
        exit()

# blank[i][j] = i번째 row, j번째 정보의 왼쪽에 있는 빈 칸 수
blank = [[0] * (len(row[i]) + 1) for i in range(n)]

# 백트래킹 여부
back = False


# i번째 row를 first step으로 채움
def first(i):
    if row[i]:
        idx = 0
        for _ in range(row[i][0]):
            board[i][idx] = 1
            idx += 1

        for j in range(1, len(row[i])):
            blank[i][j] = 1
            idx += 1
            for _ in range(row[i][j]):
                board[i][idx] = 1
                idx += 1
        
        blank[i][-1] = m - idx  # i번째 row의 제일 오른쪽 빈 칸 수


# i번째 row가 last step인지 판별
def islast(i):
    if row[i]:
        if blank[i][-1]:  # 오른쪽 끝 빈 칸이 있는 경우
            return False
        for j in range(1, len(blank[i]) - 1):
            if blank[i][j] != 1:  # 색칠된 칸들 사이에 2칸 이상의 빈 칸이 있는 경우
                return False
    return True


# i번째 row가 채워진 상태를 next step으로 변경
def next(i):
    if blank[i][-1]:  # 오른쪽 끝 빈 칸이 있는 경우
        board[i][-blank[i][-1]] = 1
        board[i][- blank[i][-1] - row[i][-1]] = 0
        blank[i][-1] -= 1
        blank[i][-2] += 1
    else:
        idx = m - row[i][-1]
        for j in range(len(blank[i]) - 2, 0, -1):
            idx -= (blank[i][j] + row[i][j - 1])
            if blank[i][j] != 1:
                blank[i][j - 1] += 1
                board[i][idx] = 0
                idx += row[i][j - 1]
                board[i][idx] = 1

                for k in range(len(row[i]) - j):
                    idx += 1
                    board[i][idx] = 0
                    blank[i][j + k] = 1
                    for _ in range(row[i][j + k]):
                        idx += 1
                        board[i][idx] = 1
                blank[i][-1] = m - idx - 1
                for _ in range(m - idx - 1):
                    idx += 1
                    board[i][idx] = 0
                break


# i번째 row까지를 col 정보와 대조해 오류가 있는지 판별
def check(i):
    for j in range(m):
        res = [0]
        for idx in range(i + 1):
            if board[idx][j]:
                res[-1] += 1
            elif res[-1] != 0:
                res.append(0)
        
        if len(res) - 1 > len(col[j]):
            return False
        
        for k in range(len(res) - 1):
            if res[k] != col[j][k]:
                return False
        
        if res[-1]:
            if len(res) - 1 == len(col[j]):
                return False
            if res[-1] > col[j][len(res) - 1]:
                return False
    return True


i = 0
while True:
    # 완성
    if i == n:
        flag = True
        for j in range(m):
            res = [0]
            for idx in range(n):
                if board[idx][j]:
                    res[-1] += 1
                elif res[-1] != 0:
                    res.append(0)
            
            if res[-1] == 0:
                res.pop()
            
            if res != col[j]:
                flag = False
                break

        if flag:
            for k in range(n):
                for j in range(m):
                    if board[k][j]:
                        print('■', end='')
                    else:
                        print('□', end='')
                print()
            break
        else:
            back = True
            i -= 1

    # 실패
    if i == -1:
        print('No Solution')
        break

    # i번째 row는 빈 행
    if not row[i]:
        if back:
            i -= 1
        else:
            if check(i):
                i += 1
            else:
                back = True
                i -= 1

    # i번째 row를 처음 방문
    elif not back:
        first(i)
        if check(i):
            i += 1
        else:
            back = True
    
    # 백트래킹 중
    elif back:
        if islast(i):
            board[i] = [0] * m
            blank[i] = [0] * (len(row[i]) + 1)
            i -= 1
        else:
            next(i)
            if check(i):
                back = False
                i += 1
