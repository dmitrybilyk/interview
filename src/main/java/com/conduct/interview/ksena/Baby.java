package com.conduct.interview.ksena;

public class Baby {

    private final String _firstName;
    private final String _lastName;
    private final String _id;
    private final Date _dateOfBirth;
    private final Weight _birthWeight;
    private Weight _currentWeight;

    private static final String DEFAULT_ID = "000000000";
    private static final int MIN_ID_LENGTH = 9;

    public Baby(String fName, String lName, String id, int day, int month, int year, int birthWeightInGrams) {
        if (fName == null || fName.isEmpty() || lName == null || lName.isEmpty()) {
            throw new IllegalArgumentException("Name or last name cannot be null or empty");
        }
        _firstName = fName;
        _lastName = lName;
        _id = (id != null && id.length() == MIN_ID_LENGTH) ? id : DEFAULT_ID;
        _dateOfBirth = new Date(day, month, year); // Using the custom Date class

        if (birthWeightInGrams < 0) {
            throw new IllegalArgumentException("Birth weight must be non-negative");
        }
        _birthWeight = new Weight(birthWeightInGrams);
        _currentWeight = _birthWeight;
    }

    public Baby(Baby other) {
        this._firstName = other._firstName;
        this._lastName = other._lastName;
        this._id = other._id;
        this._dateOfBirth = new Date(other._dateOfBirth);
        this._birthWeight = other._birthWeight;
        this._currentWeight = other._currentWeight;
    }

    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public String getId() {
        return _id;
    }

    public Date getDateOfBirth() {
        return _dateOfBirth;
    }

    public Weight getBirthWeight() {
        return _birthWeight;
    }

    public Weight getCurrentWeight() {
        return _currentWeight;
    }

    public void setCurrentWeight(Weight weightToSet) {
        if (weightToSet != null && weightToSet.getGrams() >= 0) {
            _currentWeight = weightToSet;
        }
    }

    @Override
    public String toString() {
        return "Name: " + _firstName + " " + _lastName + "\n" +
                "Id: " + _id + "\n" +
                "Date of Birth: " + _dateOfBirth + "\n" +
                "Birth Weight: " + _birthWeight.getGrams() + " grams\n" +
                "Current Weight: " + _currentWeight.getGrams() + " grams\n";
    }

    public boolean equals(Baby other) {
        return _firstName.equals(other._firstName) &&
                _id.equals(other._id) &&
                _dateOfBirth.equals(other._dateOfBirth);
    }

    public boolean areTwins(Baby other) {
        return !_firstName.equals(other._firstName) &&
                _lastName.equals(other._lastName) &&
                !_id.equals(other._id) &&
                _dateOfBirth.difference(other._dateOfBirth) <= 1;
    }

    public boolean heavier(Baby other) {
        return _currentWeight.getGrams() > other._currentWeight.getGrams();
    }

    public void updateCurrentWeight(int grams) {
        int newWeight = _currentWeight.getGrams() + grams;
        if (newWeight >= 0) {
            _currentWeight = new Weight(newWeight);
        }
    }

    public boolean older(Baby other) {
        return _dateOfBirth.before(other._dateOfBirth);
    }

    public int isWeightInValidRange(Date date) {
        int ageInDays = _dateOfBirth.difference(date);  // Количество дней с даты рождения

        // Если дата раньше даты рождения
        if (ageInDays < 0) {
            return 1; // дата до рождения
        }

        int birthWeightGrams = _birthWeight.getGrams();  // Вес при рождении
        int currentWeightGrams = _currentWeight.getGrams();  // Текущий вес

        // Если дата более чем через год после рождения
        if (ageInDays > 365) {
            return 2; // дата больше года
        }

        // Первая неделя после рождения
        if (ageInDays <= 7) {
            int minWeight = (int) (birthWeightGrams * 0.9);  // Допустимое снижение массы
            if (currentWeightGrams < minWeight) {
                return 3; // Вес ниже допустимого
            }
            return 4; // Вес в пределах нормы
        }

        // До 4 месяцев (120 дней)
        if (ageInDays <= 120) {
            int expectedWeight = birthWeightGrams + (ageInDays - 7) * 30;  // Прирост 30 г в день
            int minWeight = (int) (expectedWeight * 0.9);
            int maxWeight = (int) (expectedWeight * 1.1);

            if (currentWeightGrams < minWeight) {
                return 3; // Вес ниже допустимого
            } else if (currentWeightGrams > maxWeight) {
                return 5; // Вес выше допустимого
            }
            return 4; // Вес в пределах нормы
        }

        // От 4 до 8 месяцев (120-240 дней)
        if (ageInDays <= 240) {
            int initialGain = (120 - 7) * 30;  // Прирост за первые 120 дней
            int monthlyGain = ((ageInDays - 120) / 30) * 500;  // Прирост 500 г в месяц
            int expectedWeight = birthWeightGrams + initialGain + monthlyGain;

            int minWeight = (int) (expectedWeight * 0.9);
            int maxWeight = (int) (expectedWeight * 1.1);

            if (currentWeightGrams < minWeight) {
                return 3; // Вес ниже допустимого
            } else if (currentWeightGrams > maxWeight) {
                return 5; // Вес выше допустимого
            }
            return 4; // Вес в пределах нормы
        }

        // От 8 месяцев до года (240-365 дней)
        if (ageInDays <= 365) {
            int initialGain = (120 - 7) * 30;  // Прирост за первые 120 дней
            int gainFromFourToEightMonths = (120 / 30) * 500;  // Прирост за 4-8 месяцев
            int remainingDays = ageInDays - 240;  // Оставшиеся дни после 8 месяцев
            int gainFromEightToTwelveMonths = (remainingDays / 30) * 250;  // Прирост с 8 до 12 месяцев
            int expectedWeight = birthWeightGrams + initialGain + gainFromFourToEightMonths + gainFromEightToTwelveMonths;

            int minWeight = (int) (expectedWeight * 0.9);
            int maxWeight = (int) (expectedWeight * 1.1);

            if (currentWeightGrams < minWeight) {
                return 3; // Вес ниже допустимого
            } else if (currentWeightGrams > maxWeight) {
                return 5; // Вес выше допустимого
            }
            return 4; // Вес в пределах нормы
        }

        return 4; // Вес в пределах нормы по умолчанию
    }

}
