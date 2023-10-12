import pandas as pd
from scipy.stats import kruskal
import matplotlib.pyplot as plt

df = pd.read_csv('results.csv')
sequential_results = df['Sequential']
methods = {}

for column in df.columns[1:]:
    method_results = df[column]
    
    h_statistic, p_value = kruskal(sequential_results, method_results)
    methods[column] = p_value

for method in methods:
    print(f'P-Value ({method} vs Sequential): {methods[method]}')

#Statistical test
alpha = 0.05
significant_methods = [column for column in methods.keys() if methods[column] < alpha]
print(f'Methods with significant differences in relation to the sequential method: {significant_methods}')
print()

#Generate boxplot
plt.boxplot([sequential_results] + [df[method] for method in methods.keys()], labels=df.columns)
plt.xticks(fontsize=8)
plt.show()
