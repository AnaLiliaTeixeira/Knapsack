# Importe as bibliotecas necessárias
import pandas as pd
from scipy.stats import kruskal
from scipy.stats import mannwhitneyu
import matplotlib.pyplot as plt

# Leia o arquivo CSV com os resultados
df = pd.read_csv('results.csv')

# Selecione a coluna do método sequencial
sequential_results = df['Sequential']

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
    print(f'P-Value ({column} vs Sequential): {p_values[i]}')


# # compare samples
# stat, p = mannwhitneyu(data1, data2)
# print('Statistics=%.3f, p=%.3f' % (stat, p))
# # interpret
# alpha = 0.05
# if p > alpha:
#     print('Same distribution (fail to reject H0)')
# else:
#     print('Different distribution (reject H0)')

# # Realize o teste de significância (neste caso, o exemplo verifica se p < 0.05)
# alpha = 0.05
# significant_methods = [method_names[i] for i, p in enumerate(p_values) if p < alpha]
# print(f'Métodos com diferenças significativas em relação ao sequencial: {significant_methods}')

# Crie um gráfico de boxplot
plt.boxplot([sequential_results] + [df[column] for column in method_names], labels=df.columns)
plt.xticks(fontsize=8)
plt.show()
