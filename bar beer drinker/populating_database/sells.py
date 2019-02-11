import csv
import random

def getSells():
    count = 0
    price = 1.50
    with open('beers_ready.csv', 'r') as beercsv:
        beers = csv.reader(beercsv)
        with open('sells.csv', 'w') as sellscsv:
            sells = csv.writer(sellscsv, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
            with open('bars_ready.csv', 'r') as barscsv:
                bars = csv.reader(barscsv)
                for row_beers in beers:
                    for row_bars in  bars:
                        if count >= 5:
                            count = 0
                            break
                        elif random.randint(0,1) == 0:
                            row_sells = list()
                            row_sells.extend((row_bars[0], row_beers[0], price))
                            sells.writerow(row_sells)
                            count += 1
                            price += 0.01
getSells()