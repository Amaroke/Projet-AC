import math
from collections import deque

# Constantes de mon programme
U = 40  # Réservoir
NB_ARRETS_AUTORISE = 3  # Nombre d'arrêts autorisés
# Prix de chacunes des stations, de s à t, on mets t à +inf car il est impossible d'y faire le plein.
PRICES = [10, 26, 19, 22, 29, 39, 37, 11, 10, math.inf]

# Matrice d'adjacence représentant le graphe, avec 0 pour dire qu'il n'y a pas d'arête.
GRAPH = [
    [0, 16, 18, 23, 0, 0, 0, 0, 0, 0],
    [16, 0, 12, 0, 21, 18, 0, 0, 0, 0],
    [18, 12, 0, 14, 0, 19, 0, 0, 0, 0],
    [23, 0, 14, 0, 0, 26, 24, 0, 0, 0],
    [0, 21, 0, 0, 0, 14, 0, 14, 0, 0],
    [0, 18, 19, 26, 14, 0, 12, 16, 15, 0],
    [0, 0, 0, 24, 0, 12, 0, 0, 12, 0],
    [0, 0, 0, 0, 14, 16, 0, 0, 12, 15],
    [0, 0, 0, 0, 0, 15, 12, 12, 0, 12],
    [0, 0, 0, 0, 0, 0, 0, 15, 12, 0],
]

# Implémentation de Djikstra
def d(w, u):
    process, result = deque(), deque()
    parent, distance, visit = [-1] * \
        len(GRAPH), [math.inf] * len(GRAPH), [False] * len(GRAPH)
    distance[w], visit[w] = 0, True
    process.append(w)
    while process:
        node = process.popleft()
        shortest = distance[process[0]] if process else 0
        if node == u:
            result.append(u)
            back = parent[u]
            while back != -1:
                result.append(back)
                back = parent[back]
            return distance[u]

        for n, d in enumerate(GRAPH[node]):
            if d and not visit[n]:
                distance[n], visit[n], parent[n] = distance[node] + \
                    d, True, node
                if distance[n] <= shortest:
                    process.appendleft(n)
                else:
                    process.append(n)
    return 0


# Implémentation de C
def C(u, q, g):
    # Implémentation de l'initialisation
    if q == 1:
        dist = d(u, 9)
        if g <= dist <= U:
            return (dist - g) * PRICES[u], [u]
        else:
            return math.inf, []
    # Implémentation de la récurrence
    else:
        min = math.inf
        c = math.inf
        result = []
        for vertice in range(len(GRAPH)):
            dist = d(u, vertice)
            if vertice != u and dist <= U:
                c, path = C(vertice, q-1, 0)
                c += (dist - g) * PRICES[u]
                path = [u] + path
                if PRICES[vertice] > PRICES[u]:
                    c, path = C(vertice, q-1, U-dist)
                    c += (U - g) * PRICES[u]
                    path = [u] + path
                if c < min:
                    min = c
                    result = path
        return min, result


def solve():
    res, path = C(0, NB_ARRETS_AUTORISE, 0)
    print("La solution optimale de s à t avec au plus 3 arrêts est de " +
          str(res/100) + " €")
    print("Liste des arrêts :", path)


solve()
