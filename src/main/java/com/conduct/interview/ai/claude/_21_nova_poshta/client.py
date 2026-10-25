"""
Nova Poshta API client.

Документація: https://developers.novaposhta.ua/documentation
API ключ: особистий кабінет → Налаштування → Безпека → API ключ
"""

import json
import logging
from pathlib import Path

import requests

log = logging.getLogger("nova_poshta")

API_URL = "https://api.novaposhta.ua/v2.0/json/"
KEY_FILE = Path(__file__).parent / "api_key.txt"


class NovaPoshtaClient:
    def __init__(self, api_key: str = ""):
        if not api_key:
            api_key = KEY_FILE.read_text().strip() if KEY_FILE.exists() else ""
        if not api_key:
            raise ValueError("api_key required — поклади в api_key.txt або передай явно")
        self.api_key = api_key

    def _call(self, model: str, method: str, props: dict = None) -> list:
        payload = {
            "apiKey": self.api_key,
            "modelName": model,
            "calledMethod": method,
            "methodProperties": props or {},
        }
        r = requests.post(API_URL, json=payload, timeout=15)
        r.raise_for_status()
        body = r.json()
        if not body.get("success"):
            errors = body.get("errors", [])
            raise RuntimeError(f"API error: {errors}")
        return body.get("data", [])

    # ── Відстеження ───────────────────────────────────────────────────────────

    def track(self, *tracking_numbers: str) -> list[dict]:
        """Статус однієї або кількох посилок."""
        docs = [{"DocumentNumber": n} for n in tracking_numbers]
        return self._call("TrackingDocument", "getStatusDocuments", {"Documents": docs})

    # ── Накладні ──────────────────────────────────────────────────────────────

    def list_waybills(self, date_from: str, date_to: str = "", page: int = 1) -> list[dict]:
        """
        Список накладних за період.
        date_from / date_to у форматі 'дд.мм.рррр', напр. '01.10.2024'
        """
        props = {"DateTimeFrom": date_from, "Page": str(page), "GetFullList": "1"}
        if date_to:
            props["DateTimeTo"] = date_to
        return self._call("InternetDocument", "getDocumentList", props)

    def create_waybill(self, payload: dict) -> dict:
        """
        Створити накладну. payload — повний об'єкт methodProperties.
        Дивись create_waybill_example() нижче для прикладу.
        """
        data = self._call("InternetDocument", "save", payload)
        return data[0] if data else {}

    def delete_waybill(self, ref: str, document_number: str) -> bool:
        """Видалити чернетку накладної (лише до прийому на відділенні)."""
        data = self._call("InternetDocument", "delete",
                          {"DocumentRefs": [ref], "DocumentNumbers": [document_number]})
        return bool(data)

    # ── Контрагенти ───────────────────────────────────────────────────────────

    def get_counterparties(self, role: str = "Recipient") -> list[dict]:
        """role: Sender | Recipient"""
        return self._call("Counterparty", "getCounterparties",
                          {"CounterpartyProperty": role, "Page": "1"})

    def create_counterparty(self, first_name: str, last_name: str,
                            middle_name: str, phone: str) -> dict:
        """Додати нового отримувача (фізособа)."""
        data = self._call("Counterparty", "save", {
            "FirstName": first_name,
            "LastName": last_name,
            "MiddleName": middle_name,
            "Phone": phone,
            "CounterpartyType": "PrivatePerson",
            "CounterpartyProperty": "Recipient",
        })
        return data[0] if data else {}

    # ── Адреси і відділення ───────────────────────────────────────────────────

    def get_contact_persons(self, counterparty_ref: str) -> list[dict]:
        return self._call("Counterparty", "getCounterpartyContactPersons",
                          {"Ref": counterparty_ref, "Page": "1"})

    def get_counterparty_addresses(self, counterparty_ref: str, city_ref: str) -> list[dict]:
        return self._call("Counterparty", "getCounterpartyAddresses",
                          {"Ref": counterparty_ref, "CityRef": city_ref})

    def find_city(self, name: str) -> list[dict]:
        return self._call("Address", "getCities", {"FindByString": name})

    def find_warehouse(self, city_ref: str, search: str = "") -> list[dict]:
        props = {"CityRef": city_ref, "Page": "1", "Limit": "50"}
        if search:
            props["FindByString"] = search
        return self._call("Address", "getWarehouses", props)

    def find_street(self, city_ref: str, name: str) -> list[dict]:
        return self._call("Address", "getStreet",
                          {"CityRef": city_ref, "FindByString": name, "Limit": "20"})

    def save_recipient_address(self, counterparty_ref: str, city_ref: str,
                                street_ref: str, building: str, flat: str = "") -> dict:
        props = {
            "CounterpartyRef": counterparty_ref,
            "StreetRef": street_ref,
            "BuildingNumber": building,
            "CityRef": city_ref,
        }
        if flat:
            props["Flat"] = flat
        data = self._call("Address", "save", props)
        return data[0] if data else {}
