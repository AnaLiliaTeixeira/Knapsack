# Import package
import pandas as pd
from numpy import mean
from numpy import std
from scipy.stats import kruskal

# Read from the scores.csv using pandas
df = pd.read_csv('results.csv')

# Realize um teste estatístico (por exemplo, teste t) para cada método
sequencial_results = df["Sequencial"]
method3_results = df["Method 1:Thread 2"]

# summarize
print('data1: mean=%.3f stdv=%.3f' % (mean(sequencial_results), std(sequencial_results)))
print('data2: mean=%.3f stdv=%.3f' % (mean(method3_results), std(method3_results)))

# Realize o Teste de Kruskal-Wallis
h_statistic, p_value = kruskal(sequencial_results, method3_results)

# Verifique se há diferenças significativas
alpha = 0.05  # Nível de significância (geralmente 0.05)
if p_value < alpha:
    print("Há diferenças significativas entre os métodos.")
else:
    print("Não há diferenças significativas entre os métodos.")
