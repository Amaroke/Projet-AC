# Partie I :

### Q1.
> Voir Evaluator.java

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
> Voir Simplificator.java