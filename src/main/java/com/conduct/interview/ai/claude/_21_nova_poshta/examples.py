"""
Приклади використання Nova Poshta API client.
Запуск: venv/bin/python examples.py
"""

from client import NovaPoshtaClient

np = NovaPoshtaClient()  # читає api_key.txt


# ── Відстеження посилки ───────────────────────────────────────────────────────

def example_track():
    results = np.track("59000000000000")  # заміни на свій номер
    for item in results:
        print(f"№ {item['Number']}")
        print(f"  Статус:    {item['Status']}")
        print(f"  Де:        {item['WarehouseRecipient']}")
        print(f"  Дата:      {item['ScheduledDeliveryDate']}")
        print()


# ── Список своїх накладних за жовтень ────────────────────────────────────────

def example_list_waybills():
    docs = np.list_waybills("01.10.2024", "31.10.2024")
    for d in docs:
        print(f"{d['IntDocNumber']}  {d['RecipientFullName']}  {d['StateName']}")


# ── Пошук міста і відділення ─────────────────────────────────────────────────

def example_find_warehouse():
    cities = np.find_city("Львів")
    lviv = cities[0]
    print(f"Місто: {lviv['Description']}  Ref: {lviv['Ref']}")

    warehouses = np.find_warehouse(lviv["Ref"], "Відділення №1")
    for w in warehouses[:3]:
        print(f"  {w['Description']}")


# ── Створення накладної ───────────────────────────────────────────────────────

def example_create_waybill():
    """
    Мінімальний приклад. Для реального використання потрібно:
    - Ref відправника (getCounterparties)
    - Ref відділення відправника і отримувача (getWarehouses)
    - Ref отримувача (save Counterparty або існуючий)
    """
    result = np.create_waybill({
        "PayerType": "Sender",
        "PaymentMethod": "Cash",
        "CargoType": "Cargo",
        "Weight": "1",
        "ServiceType": "WarehouseWarehouse",
        "SeatsAmount": "1",
        "Description": "Одяг",
        "Cost": "500",

        "CitySender": "<Ref міста відправника>",
        "Sender": "<Ref відправника>",
        "SenderAddress": "<Ref відділення відправника>",
        "ContactSender": "<Ref контакту відправника>",
        "SendersPhone": "380991234567",

        "CityRecipient": "<Ref міста отримувача>",
        "Recipient": "<Ref отримувача>",
        "RecipientAddress": "<Ref відділення отримувача>",
        "ContactRecipient": "<Ref контакту отримувача>",
        "RecipientsPhone": "380997654321",
    })
    print(f"Накладна: {result.get('IntDocNumber')}  Ref: {result.get('Ref')}")


if __name__ == "__main__":
    print("=== Відстеження ===")
    example_track()

    print("=== Накладні за жовтень ===")
    example_list_waybills()

    print("=== Відділення Львова ===")
    example_find_warehouse()
