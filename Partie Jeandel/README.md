# Partie I :

### Q1.

Fichier Evaluator.java :

```java
import java.util.HashMap;
import java.util.Vector;

public class Evaluator {

    final HashMap<String, Integer> variables = new HashMap<>();

    int eval(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                if (assign.rhs instanceof Variable) {
                    variables.put(assign.lhs, variables.get(((Variable) assign.rhs).var));
                } else {
                    variables.put(assign.lhs, ((Entier) assign.rhs).x);
                }
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value = getValueT(assignOperator.t0);
                int t1Value = getValueT(assignOperator.t1);
                switch (assignOperator.op.charAt(0)) {
                    case '+' -> variables.put(assignOperator.lhs, t0Value + t1Value);
                    case '-' -> variables.put(assignOperator.lhs, t0Value - t1Value);
                    case '*' -> variables.put(assignOperator.lhs, t0Value * t1Value);
                    default -> throw new Exception("Erreur dans l'opérateur");
                }
            }
        }
        return variables.getOrDefault("x", 0);
    }

    public int getValueT(Value t) throws Exception {
        if (t instanceof Variable) {
            String stringT = ((Variable) t).var;
            if (variables.containsKey(stringT)) {
                return variables.get(stringT);
            } else {
                throw new Exception("La variable " + stringT + " n'existe pas");
            }
        } else {
            return ((Entier) t).x;
        }
    }
}
```

Pour l'utiliser il suffit de faire :
> java -jar projet.jar evaluation `fichier.txt`

Avec `fichier.txt` le programme à évaluer.

### Q2.

Les entiers de JAVA peuvent aller jusqu'à 2 147 483 647.
Si on fait l'addition d'un nombre avec 2 147 483 647, on se retrouve avec un entier négatif.
L'addition ne fonctionnera pas, car le résultat dépasse la "limite binaire" des entiers 32 bits.
<br>Exemple avec un entier 4 bits :
On veut additionner 15 et 1,
et donc en base 2 1111 et 0001,
on obtient 10000,
mais cet entier sur 4 bits est coupé en 0000, on a donc un résultat de 0 pour l'addition de 15 et de 1.

Exemple de programme qui ne fonctionne pas :

```
y = 2147483647
z = 1
x = y + z
```

### Q3.

Fichier Simplificator.java :

```java
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class Simplificator {
    final Vector<Instruction> instructions = new Vector<>();
    final HashMap<String, Integer> variables = new HashMap<>();

    Vector<Instruction> simplify(Vector<Instruction> instructions) throws Exception {
        for (Instruction i : instructions) {
            // On ajoute toutes les asignations.
            if (i instanceof Assign assign) {
                if (assign.rhs instanceof Variable) {
                    this.instructions.add(i);
                    variables.put(assign.lhs, variables.get(((Variable) assign.rhs).var));
                } else {
                    if (((Entier) assign.rhs).x == 1 || ((Entier) assign.rhs).x == 0) {
                        this.instructions.add(i);
                        variables.put(assign.lhs, ((Entier) assign.rhs).x);
                    } else {
                        this.instructions.add(i);
                        variables.put(assign.lhs, -1);
                    }
                }
            } else { // On ajoute les opérations utiles.
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value = getValueT(assignOperator.t0);
                int t1Value = getValueT(assignOperator.t1);
                boolean t0Equal0 = (t0Value == 0);
                boolean t1Equal0 = (t1Value == 0);
                boolean t0Equal1 = (t0Value == 1);
                boolean t1Equal1 = (t1Value == 1);
                boolean t0Andt1Equal0 = t0Equal0 && t1Equal0;
                boolean t0Ort1Equal0 = !t0Andt1Equal0 && (t0Equal0 || t1Equal0);
                boolean t0Andt1Equal1 = t0Equal1 && t1Equal1;
                boolean t0Ort1Equal1 = !t0Andt1Equal1 && (t0Equal1 || t1Equal1);
                switch (assignOperator.op.charAt(0)) {
                    case '+' -> {
                        if (t0Andt1Equal0) { // X = 0 + 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(0)));
                        } else if (t0Ort1Equal0 && t0Ort1Equal1) { // X = 0 + 1 OR X = 1 + 0
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(1)));
                        } else if (t0Ort1Equal0) { // X = inc + 0 OR X = 0 + inc
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(t1Value)));
                        } else if (t0Andt1Equal1) { // X = 1 + 1
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(new AssignOperator(assignOperator.lhs,
                                    "+", new Entier(1),
                                    new Entier(1)));
                        } else { // X = inc + inc OR X = inc + 1 OR 1 + inc
                            variables.put(assignOperator.lhs, -1);
                            if (t0Equal1) {
                                this.instructions.add(new AssignOperator(assignOperator.lhs,
                                        "+", new Entier(1),
                                        new Entier(t1Value)));
                            } else if (t1Equal1) {
                              this.instructions.add(new AssignOperator(assignOperator.lhs, "+",
                                      new Entier(t0Value), new Entier(1)));
                            } else {
                              this.instructions.add(i);
                            }
                        }
                    }
                    case '-' -> {
                        if (t0Andt1Equal0) { // X = 0 - 0
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(0)));
                        } else if (t0Equal1 && t1Equal0) { // X = 1 - 0
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(1)));
                        } else if (t0Value == t1Value && t0Value == 1) { // X = 1 - 1
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(0)));
                        } else { // X = 0 - 1 OR X = inc - 1 OR inc - inc OR inc - 0
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(i);
                        }
                    }
                    case '*' -> {
                        if (t0Equal0 || t1Equal0) { // X = 0 * 0 OR X = 1 * 0 OR X = 0 * 1
                            // OR X = inc * 0 OR X = 0 * inc
                            variables.put(assignOperator.lhs, 0);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(0)));
                        } else if (t0Equal1 && t1Equal1) { // X = 1 * 1
                            variables.put(assignOperator.lhs, 1);
                            this.instructions.add(new Assign(assignOperator.lhs,
                                    new Entier(1)));
                        } else if (t0Equal1 || t1Equal1) { // X = 1 * inc OR X = inc * 1
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(i);
                        } else { // X = inc * 1 OR 1 * inc OR X = inc * inc
                            variables.put(assignOperator.lhs, -1);
                            this.instructions.add(i);
                        }
                    }
                    default -> throw new Exception("Erreur dans l'opérateur");
                }
            }
        }

        // On supprime toutes les instructions qui sont après la dernière affectation de x.
        int lastXPosition = -1;
        for (int i = 0; i < this.instructions.size(); i++) {
            Instruction instructionI = this.instructions.get(i);
            String instructionVarI = (instructionI instanceof Assign) ?
                    ((Assign) instructionI).lhs :
                    ((AssignOperator) instructionI).lhs;
            if (instructionVarI.equals("x")) {
                lastXPosition = i;
            }
        }
        if (lastXPosition == -1) {
            throw new Exception("Pas d'affectation de x dans le programme");
        }
        while (lastXPosition + 1 != this.instructions.size()) {
            this.instructions.remove(lastXPosition + 1);
        }

        // On supprime ensuite toutes les instructions inutiles
        // (qui ne sont pas utiles à la dernière affectation de x).
        boolean instructionsClear = false;
        while (!instructionsClear) {
            int i;
            int instructionSize = this.instructions.size() - 1;
            for (i = 0; i < instructionSize; i++) {
                boolean instructionIToDelete = true;
                Instruction instructionI = this.instructions.get(i);
                String instructionVarI = (instructionI instanceof Assign)
                        ? ((Assign) instructionI).lhs :
                        ((AssignOperator) instructionI).lhs;
                for (int j = i + 1; j < this.instructions.size(); j++) {
                  Instruction instructionJ = this.instructions.get(j);
                  String instructionVarJ = (instructionJ instanceof Assign) ?
                          ((Assign) instructionJ).lhs :
                          ((AssignOperator) instructionJ).lhs;
                  String t0Var = (instructionJ instanceof AssignOperator) ?
                          (((AssignOperator) instructionJ).t0 instanceof Variable)
                                  ? ((Variable) ((AssignOperator) instructionJ).t0).var
                                  : "" : (instructionJ instanceof Assign) ?
                          (((Assign) instructionJ).rhs instanceof Variable) ?
                                  ((Variable) ((Assign) instructionJ).rhs).var
                                  : "" : "";
                  String t1Var = (instructionJ instanceof AssignOperator) ?
                          (((AssignOperator) instructionJ).t1 instanceof Variable) ?
                                  ((Variable) ((AssignOperator) instructionJ).t1).var
                                  : "" : (instructionJ instanceof Assign) ?
                          (((Assign) instructionJ).rhs instanceof Variable) ?
                                  ((Variable) ((Assign) instructionJ).rhs).var
                                  : "" : "";
                  if ((Objects.equals(instructionVarI, t0Var)) || (Objects.equals(instructionVarI, t1Var))) {
                    instructionIToDelete = false;
                    break;
                  }
                  if (instructionVarI.equals(instructionVarJ)) {
                    break;
                  }
                }
                if (instructionIToDelete) {
                    this.instructions.remove(i);
                    break;
                }
            }
            instructionsClear = (i == instructionSize);
        }
        return this.instructions;
    }

    public int getValueT(Value t) throws Exception {
        if (t instanceof Variable) {
            String stringT = ((Variable) t).var;
            if (variables.containsKey(stringT)) {
                return variables.get(stringT);
            } else {
                throw new Exception("La variable " + stringT + " n'existe pas");
            }
        } else {
            return ((Entier) t).x;
        }
    }
}
```

> java -jar projet.jar simplification `fichier.txt`

Avec `fichier.txt` le programme à simplifier.

Les simplifications effectuées sont précisées dans les commentaires du code, et les fichiers de tests sont disponnibles
dans le répertoire "tests", fournis avec le .jar et ce README.

### Q4.

Fichier EvaluatorModulo.java :

```java
import java.util.HashMap;
import java.util.Vector;

public class EvaluatorModulo {

    final HashMap<String, Integer> variables = new HashMap<>();

    int evaluer(Vector<Instruction> instructions, int p) throws Exception {
        for (Instruction i : instructions) {
            if (i instanceof Assign assign) {
                if (assign.rhs instanceof Variable) {
                    variables.put(assign.lhs, variables.get(((Variable) assign.rhs).var));
                } else {
                    variables.put(assign.lhs, ((Entier) assign.rhs).x);
                }
            } else {
                AssignOperator assignOperator = (AssignOperator) i;
                int t0Value = getValueT(assignOperator.t0);
                int t1Value = getValueT(assignOperator.t1);
                switch (assignOperator.op.charAt(0)) {
                    case '+' -> variables.put(assignOperator.lhs, t0Value + t1Value);
                    case '-' -> variables.put(assignOperator.lhs, t0Value - t1Value);
                    case '*' -> variables.put(assignOperator.lhs, multiply(t0Value, t1Value, p));
                    default -> throw new Exception("Erreur dans l'opérateur");
                }
            }
        }
        return variables.getOrDefault("x", 0);
    }

    private int multiply(int x, int y, int p) {
        int result = 0;
        while (y != 0) {
            if ((y & 1) != 0) result = (result + x) % p;
            y >>= 1;
            x = (2 * x) % p;
        }
        return result;
    }

    public int getValueT(Value t) throws Exception {
        if (t instanceof Variable) {
            String stringT = ((Variable) t).var;
            if (variables.containsKey(stringT)) {
                return variables.get(stringT);
            } else {
                throw new Exception("La variable " + stringT + " n'existe pas");
            }
        } else {
            return ((Entier) t).x;
        }
    }
}
```

Même principe que Evaluator.java, cependant on fait les multiplications modulo p, avec p fourni en paramètre à la
fonction.

Pour l'utiliser il suffit de faire :
> java -jar projet.jar evaluationModulo `fichier.txt` `p`

Avec `fichier.txt` le programme à évaluer et `p` le paramètre pour le modulo.

### Q5.

Fichier Comparator.java :

```java
import java.util.Vector;

public class Comparator {

    boolean comparer(Vector<Instruction> instructions1,
                     Vector<Instruction> instructions2) throws Exception {
        EvaluatorModulo evaluator = new EvaluatorModulo();
        int p = RandomPrime.randomPrime();
        return evaluator.evaluer(instructions2, p) ==
                evaluator.evaluer(instructions1, p);
    }

}
```

Tire un nombre premier aléatoire, puis compare le résultat ("l'évaluation") de deux programmes retourne vrai s'ils
sont égaux, faux sinon.

Pour l'utiliser il suffit de faire :
> java -jar projet.jar comparer `fichier1.txt` `fichier2.txt`

Avec `fichier1.txt` et `fichier2.txt` les programmes à comparer.

### Q6.

Pour répondre à cette question, nous utiliserons une récurrence :<br>
Initialisation : <br>
`n = 0, 2^2*0 = 1`<br>
`n = 1, 2^2*1 = 4`<br>

Récurrence :<br>
(1) On part du principe que le résultat d'un programme de taille `n` est inférieur à `2^2*n`.<br>

Montrons que le résultat d'un programme de taille `n+1` est inférieur à `2^2*(n+1)` :<br>

On part d'un programme de taille `n` auquel on ajoute une instruction, on a donc un programme de taille `n+1`.<br>
Le résultat de l'instruction est toujours inférieur à `2^2*n` grâce à (1).<br>
Et donc le résultat d'un programme de taille `n+1` est également inférieur à `2^2*n`.<br>

Grâce à l'initialisation et notre récurrence, on peut affirmer que le résultat d'un programme de taille `n` est
inférieur à `2^2*n`.

### Q7.

- Si p1 et p2 calculent le même nombre, peu importe le modulo choisi, cela retournera le même résultat, et donc le
  programme retournera toujours vrai.
- Si p1 et p2 calculent des nombres différents, cela signifie qu'ils suivent des algorithmes
  différents ou qu'ils font des calculs différents à un certain moment.
  Considérons que les programmes p1 et p2 qui calculent les nombres a et b.
  Si a et b sont différents, alors ils ne sont pas divisibles par les mêmes nombres premiers.
  Cela signifie que si nous choisissons un nombre premier p tel que a est divisible par p et b n'est pas divisible par
  p, alors le programme a une probabilité de 1/2 de répondre Faux.
  Il y a environ 50 millions de nombres premiers inférieurs à 2^30, donc il y a de nombreux choix possibles pour p.
  La probabilité que p1 et p2 calculent des nombres divisibles par les mêmes nombres premiers est donc très faible (<
  1/2).

### Q8.

La longueur du programme n'influant pas le nombre de nombres premiers, le résultat précédent est encore valide.

### Q9.

Polynomial Identity Testing (PIT) est un domaine de l'informatique qui étudie les méthodes pour déterminer si deux
polynômes sont identiques ou non.<br>
La méthode de Schwartz-Zippel est une méthode célèbre pour résoudre ce problème.<br>
La méthode de Schwartz-Zippel utilise un test probabiliste pour déterminer si deux polynômes sont identiques.<br>
Elle fournit donc une solution probabiliste,
en testant simplement au hasard les entrées et en vérifiant la sortie.<br>
C'était le premier algorithme PIT en temps polynomial randomisé à s'avérer correct.<br>
Plus le domaine à partir duquel les entrées sont tirées est grand, moins Schwartz – Zippel est susceptible
d'échouer. <br>
L'algorithme de Chen-Kao (sur les rationnels) ou l'algorithme de Lewin-Vadhan (sur n'importe quel nombre) nécessite
moins de ressources au prix d'un temps d'exécution plus long.<br>
Elle consiste à choisir au hasard des entrées, et à évaluer les deux polynômes en ces entrées.<br>
Si les valeurs obtenues pour les deux polynômes sont différentes pour au moins un des paramètres choisis,
alors les polynômes sont différents avec une probabilité proche de 1.<br>
Si les valeurs obtenues pour les deux polynômes sont identiques pour tous les paramètres choisis, alors ils sont
probabilistiquement identiques.<br>
La précision de cette méthode peut être améliorée en augmentant le nombre d'entrées choisies au hasard.<br>

Un algortithme pour cette méthode serait donc :

* Choisir au hasard un nombre d'entiers (de paramètres).
* Évaluer les deux polynômes avec ces paramètres.
* Si les valeurs obtenues pour les deux polynômes sont différentes pour au moins un des entiers choisis, alors les
  polynômes sont différents.
* Si les valeurs obtenues pour les deux polynômes sont identiques pour tous les entiers choisis, alors ils sont
  probabilistiquement identiques.

Sources :
> https://cs.stanford.edu/~mpkim/notes/lec5.pdf <br>
> https://www.cs.ubc.ca/~nickhar/W12/Lecture9Notes.pdf <br>
> https://www.youtube.com/watch?v=HJLEdCSJpMI <br>
