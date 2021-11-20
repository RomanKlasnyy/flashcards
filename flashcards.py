from collections import defaultdict
import random
import argparse

cards_dict = {}
statistics = defaultdict(int)
logs = []
parser = argparse.ArgumentParser()
parser.add_argument("-i", "--import_from", default=None)
parser.add_argument("-e", "--export_to", default=None)
args = parser.parse_args()


def print_log(entry):
    print(entry)
    logs.append(entry)


def input_log():
    result = input()
    logs.append(result)
    return result


def add():
    print_log(f'The card:')
    while True:
        card = input_log()
        if card not in cards_dict.keys():
            break
        else:
            print_log(f'The term "{card}" already exists. Try again:')
    print_log(f'The definition of the card:')
    while True:
        definition = input_log()
        if definition not in cards_dict.values():
            break
        else:
            print_log(f'The definition "{definition}" already exists. Try again:')
    cards_dict[card] = definition
    print_log(f'The pair ("{card}":"{definition}") has been added.')


def remove():
    print_log('Which card?')
    rem = input_log()
    if rem in cards_dict:
        del cards_dict[rem]
        if rem in statistics:
            del statistics[rem]
        print_log('The card has been removed.')
    else:
        print_log(f'Can\'t remove "{rem}": there is no such card.')


def import_():
    try:
        print_log('File name:')
        file = input_log()
        with open(file, 'r') as f:
            count = 0
            for line in f.readlines():
                c, d, s = line.split('||')
                cards_dict[c.strip()] = d.strip()
                statistics[c] = int(s)
                count += 1
            print_log(f'{count} cards have been loaded.')
    except FileNotFoundError:
        print_log('File not found.')


def cli_import():
    file = args.import_from
    try:
        with open(file, 'r') as f:
            count = 0
            for line in f.readlines():
                c, d, s = line.split('||')
                cards_dict[c.strip()] = d.strip()
                statistics[c] = int(s)
                count += 1
            print_log(f'{count} cards have been loaded.')
    except FileNotFoundError:
        print_log('File not found.')


def export():
    print_log('File name:')
    file = input_log()
    with open(file, 'w') as f:
        for c, d in cards_dict.items():
            f.write(f'{c}||{d}||{statistics[c]}\n')
    print_log(f'{len(cards_dict)} cards have been saved.')


def cli_export():
    file = args.export_to
    with open(file, 'w') as f:
        for c, d in cards_dict.items():
            f.write(f'{c}||{d}||{statistics[c]}\n')
    print_log(f'{len(cards_dict)} cards have been saved.')


def ask():
    print_log('How many times to ask?')
    inp = int(input_log())
    for _ in range(inp):
        card = random.choice([x for x in cards_dict.keys()])
        print_log(f'Print the definition of "{card}":')
        inp = input_log()
        if inp == cards_dict[card]:
            print_log('Correct!')
        elif inp in cards_dict.values():
            print_log(f'Wrong. The right answer is "{cards_dict[card]}", but your definition is correct for \
"{list(cards_dict.keys())[list(cards_dict.values()).index(inp)]}".')
            statistics[card] += 1
        else:
            print_log(f'Wrong. The right answer is "{cards_dict[card]}".')
            statistics[card] += 1


def save_log():
    print_log('File name:')
    file_name = input_log()
    with open(file_name, 'w') as f:
        f.write("\n".join(logs))
    print_log('The log has been saved.')


def hardest():
    if statistics:
        if max(statistics.values()) == 0:
            print_log('There are no cards with errors.')
        else:
            errors = statistics.values()
            hard_num = list(errors).count(max(errors))
            if hard_num == 1:
                print_log(f'The hardest card is "{max(statistics, key=statistics.get)}".\
You have {max(errors)} errors answering it')
            else:
                hard_list = [f'"{x}"' for x in statistics.keys() if statistics[x] == max(errors)]
                print_log(f'The hardest cards are {", ".join(hard_list)}')
    else:
        print_log('There are no cards with errors.')


def main():
    if args.import_from:
        cli_import()
    while True:
        print_log('Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):')
        action = input_log()
        if action == "add":
            add()
        elif action == "remove":
            remove()
        elif action == "import":
            import_()
        elif action == "export":
            export()
        elif action == "ask":
            ask()
        elif action == "exit":
            print_log('Bye bye!')
            if args.export_to:
                cli_export()
            break
        elif action == "log":
            save_log()
        elif action == "hardest card":
            hardest()
        elif action == "reset stats":
            statistics.clear()
            print_log('Card statistics have been reset.')
        elif action == "stats":
            print(statistics)


if __name__ == '__main__':
    main()
