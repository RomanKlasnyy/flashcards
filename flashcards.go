package main

import (
	"bufio"
	"fmt"
	"math/rand"
	"os"
	"sort"
	"strconv"
	"strings"
	"time"
)

var cardsDict = make(map[string]string)
var statistics = make(map[string]int)
var logs []string

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	rand.Seed(time.Now().UnixNano())

	for {
		fmt.Println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
		scanner.Scan()
		input := scanner.Text()

		switch input {
		case "add":
			add(scanner)
		case "remove":
			remove(scanner)
		case "import":
			importCards(scanner)
		case "export":
			export(scanner)
		case "ask":
			ask(scanner)
		case "exit":
			fmt.Println("Bye bye!")
			os.Exit(0)
		case "log":
			saveLog(scanner)
		case "hardest card":
			hardest()
		case "reset stats":
			statistics = make(map[string]int)
			fmt.Println("Card statistics have been reset.")
		case "stats":
			fmt.Println(statistics)
		default:
			fmt.Println("Invalid action.")
		}
	}
}

func add(scanner *bufio.Scanner) {
	fmt.Println("The card:")
	scanner.Scan()
	card := scanner.Text()

	if _, exists := cardsDict[card]; exists {
		fmt.Printf("The term \"%s\" already exists. Try again:\n", card)
		return
	}

	fmt.Println("The definition of the card:")
	scanner.Scan()
	definition := scanner.Text()

	if _, exists := findKey(cardsDict, definition); exists {
		fmt.Printf("The definition \"%s\" already exists. Try again:\n", definition)
		return
	}

	cardsDict[card] = definition
	fmt.Printf("The pair (\"%s\":\"%s\") has been added.\n", card, definition)
}

func remove(scanner *bufio.Scanner) {
	fmt.Println("Which card?")
	scanner.Scan()
	rem := scanner.Text()

	if _, exists := cardsDict[rem]; exists {
		delete(cardsDict, rem)
		delete(statistics, rem)
		fmt.Println("The card has been removed.")
	} else {
		fmt.Printf("Can't remove \"%s\": there is no such card.\n", rem)
	}
}

func importCards(scanner *bufio.Scanner) {
	fmt.Println("File name:")
	scanner.Scan()
	file := scanner.Text()

	fileHandle, err := os.Open(file)
	if err != nil {
		fmt.Println("File not found.")
		return
	}
	defer fileHandle.Close()

	fileScanner := bufio.NewScanner(fileHandle)
	count := 0
	for fileScanner.Scan() {
		line := fileScanner.Text()
		parts := strings.Split(line, "||")
		c := strings.TrimSpace(parts[0])
		d := strings.TrimSpace(parts[1])
		s, _ := strconv.Atoi(strings.TrimSpace(parts[2]))
		cardsDict[c] = d
		statistics[c] = s
		count++
	}
	fmt.Printf("%d cards have been loaded.\n", count)
}

func export(scanner *bufio.Scanner) {
	fmt.Println("File name:")
	scanner.Scan()
	file := scanner.Text()

	fileHandle, err := os.Create(file)
	if err != nil {
		fmt.Println("An error occurred while exporting.")
		return
	}
	defer fileHandle.Close()

	for card, definition := range cardsDict {
		fileHandle.WriteString(fmt.Sprintf("%s||%s||%d\n", card, definition, statistics[card]))
	}
	fmt.Printf("%d cards have been saved.\n", len(cardsDict))
}

func ask(scanner *bufio.Scanner) {
	fmt.Println("How many times to ask?")
	scanner.Scan()
	inp, _ := strconv.Atoi(scanner.Text())

	keys := make([]string, 0, len(cardsDict))
	for k := range cardsDict {
		keys = append(keys, k)
	}

	for i := 0; i < inp; i++ {
		card := keys[rand.Intn(len(keys))]
		fmt.Printf("Print the definition of \"%s\":\n", card)
		scanner.Scan()
		input := scanner.Text()
		if input == cardsDict[card] {
			fmt.Println("Correct!")
		} else if correctCard, exists := findKey(cardsDict, input); exists {
			fmt.Printf("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".\n", cardsDict[card], correctCard)
			statistics[card]++
		} else {
			fmt.Printf("Wrong. The right answer is \"%s\".\n", cardsDict[card])
			statistics[card]++
		}
	}
}

func findKey(m map[string]string, val string) (string, bool) {
	for key, value := range m {
		if value == val {
			return key, true
		}
	}
	return "", false
}

func saveLog(scanner *bufio.Scanner) {
	fmt.Println("File name:")
	scanner.Scan()
	fileName := scanner.Text()

	fileHandle, err := os.Create(fileName)
	if err != nil {
		fmt.Println("An error occurred while saving the log.")
		return
	}
	defer fileHandle.Close()

	for _, log := range logs {
		fileHandle.WriteString(log + "\n")
	}
	fmt.Println("The log has been saved.")
}

func hardest() {
	if len(statistics) == 0 {
		fmt.Println("There are no cards with errors.")
		return
	}

	var maxErrors int
	for _, v := range statistics {
		if v > maxErrors {
			maxErrors = v
		}
	}

	if maxErrors == 0 {
		fmt.Println("There are no cards with errors.")
		return
	}

	var hardestCards []string
	for k, v := range statistics {
		if v == maxErrors {
			hardestCards = append(hardestCards, "\""+k+"\"")
		}
	}

	sort.Strings(hardestCards)
	if len(hardestCards) == 1 {
		fmt.Printf("The hardest card is %s. You have %d errors answering it.\n", hardestCards[0], maxErrors)
	} else {
		fmt.Printf("The hardest cards are %s\n", strings.Join(hardestCards, ", "))
	}
}
