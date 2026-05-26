import random

words = ["python", "lucky", "orange", "eagle", "hemanth"]

word = random.choice(words)

guess_letters = []
attempts = 6

print("🎮 Welcome to Hangman Game!")

while attempts > 0:
    display_word = ""

    for letter in word:
        if letter in guess_letters:
            display_word += letter + " "
        else:
            display_word += "_ "

    print("\nWord:", display_word.strip())

    if "_" not in display_word:
        print("💕 Congratulations! You guessed the word:", word)
        break

    guess = input("Guess a letter: ").lower().strip()

    if len(guess) != 1 or not guess.isalpha():
        print("⚠️ Please enter only one alphabet letter")
        continue

    if guess in guess_letters:
        print("⚠️ You already guessed that letter.")
        continue

    guess_letters.append(guess)

    if guess in word:
        print("✅ Correct guess!")
    else:
        attempts -= 1
        print("❌ Wrong guess!")
        print("Remaining attempts:", attempts)

if attempts == 0:
    print("☠️ Game Over! The correct word was:", word)