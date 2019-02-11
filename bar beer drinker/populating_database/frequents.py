import csv
import random

def getFrequents():
    count = 0
    with open('frequents.csv', 'w') as frequentscsv:
        frequents = csv.writer(frequentscsv, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
        with open('bars_ready.csv', 'r') as barscsv:
            bars = csv.reader(barscsv)
            for row_bars in bars:
                with open('drinkers_ready.csv', 'r') as drinkerscsv:
                    drinkers = csv.reader(drinkerscsv)
                    for row_drinkers in  drinkers:
                        if count >= 5:
                            count = 0
                            break
                        elif random.randint(0,75) == 0 and row_bars[4] == row_drinkers[3]:
                            row_frequents = list()
                            row_frequents.extend((row_drinkers[0], row_bars[0]))
                            frequents.writerow(row_frequents)
                            count += 1
getFrequents()