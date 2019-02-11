import csv
import random

def getTransactions():
    id = 0
    count = 0
    food = ['wings', 'fries', 'burger', 'soda', 'milkshake']
    with open('sells_ready.csv', 'r') as sellscsv:
        sells = csv.reader(sellscsv)
        with open('drinkers_ready.csv', 'r') as drinkerscsv:
            drinkers = csv.reader(drinkerscsv)
            with open('transactions.csv', 'w') as transactionscsv:
                transactions = csv.writer(transactionscsv, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
                with open('items.csv', 'w') as itemscsv:
                    items = csv.writer(itemscsv, escapechar='%', lineterminator='\n', quoting=csv.QUOTE_NONE)
                    for row_sells in sells:
                        for i in range(0,10):
                            count += 1
                            try:
                                row_drinkers = drinkers.__next__()
                            except StopIteration:
                                drinkerscsv = open('drinkers_ready.csv', 'r')
                                drinkers = csv.reader(drinkerscsv)
                            id += 1
                            time = random.randint(1400, 2300)
                            tax = random.randint(0, 50) + random.randint(0, 99) / 100
                            tip = random.randint(0, 50) + random.randint(0, 99) / 100
                            total = random.randint(0, 100) + tax + tip
                            drinker = row_drinkers[0]
                            bar = row_sells[0]
                            beer = row_sells[1]
                            row_trans = list()
                            row_items1 = list()
                            row_items2 = list()
                            row_trans.extend((str(id), drinker, bar, str(time), str(tax), str(tip), str(total)))
                            row_items1.extend((str(id), beer, 'A'))
                            row_items2.extend((str(id), food[random.randint(0,4)], 'NA'))
                            transactions.writerow(row_trans)
                            items.writerow(row_items1)
                            items.writerow(row_items2)
                    print(count)
getTransactions()