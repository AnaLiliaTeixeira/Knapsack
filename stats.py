# Importe as bibliotecas necessárias
import pandas as pd
from scipy.stats import kruskal
import matplotlib.pyplot as plt

# Leia o arquivo CSV com os resultados
df = pd.read_csv('results.csv')

# Selecione a coluna do método sequencial
sequencial_results = df['Sequencial']

# Inicialize uma lista para armazenar os p-values
p_values = []

# Inicialize uma lista para armazenar os nomes dos métodos
method_names = []

# Percorra as colunas do DataFrame, exceto a primeira que é a coluna 'Sequencial'
for column in df.columns[1:]:
    method_results = df[column]
    
    # Compare o método atual com o método sequencial usando o teste de Kruskal-Wallis
    h_statistic, p_value = kruskal(sequencial_results, method_results)
    
    # Armazene o p-value na lista
    p_values.append(p_value)
    
    # Armazene o nome do método na lista
    method_names.append(column)

# Imprima os valores de p-value para cada comparação
for i, column in enumerate(method_names):
    print(f'P-Value ({column} vs Sequencial): {p_values[i]}')

# Realize o teste de significância (neste caso, o exemplo verifica se p < 0.05)
alpha = 0.05
significant_methods = [method_names[i] for i, p in enumerate(p_values) if p < alpha]
print(f'Métodos com diferenças significativas em relação ao sequencial: {significant_methods}')

# Crie um gráfico de boxplot
plt.boxplot([sequencial_results] + [df[column] for column in method_names], labels=df.columns)
plt.show()
