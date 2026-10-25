"""Nova Poshta parcels dashboard."""

import logging
import os
from datetime import datetime, timedelta

from flask import Flask, jsonify, render_template_string, request

from client import NovaPoshtaClient

log = logging.getLogger("np_web")
app = Flask(__name__)
np  = NovaPoshtaClient()

HTML = """<!DOCTYPE html>
<html lang="uk">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Мої посилки — Нова Пошта</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0 }
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
         background: #f5f5f5; color: #222; }
  header { background: #c8102e; color: #fff; padding: 16px 24px;
           display: flex; align-items: center; gap: 12px; }
  header h1 { font-size: 1.2rem; font-weight: 600; }
  .sub { font-size: .8rem; opacity: .75; }
  .controls { padding: 14px 24px; display: flex; gap: 10px; flex-wrap: wrap;
              align-items: center; background: #fff; border-bottom: 1px solid #e0e0e0; }
  /* modal */
  .overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,.45); z-index:100;
             align-items:center; justify-content:center; }
  .overlay.open { display:flex; }
  .modal { background:#fff; border-radius:10px; padding:24px; width:420px; max-width:95vw;
           max-height:90vh; overflow-y:auto; box-shadow:0 4px 24px rgba(0,0,0,.2); }
  .modal h2 { font-size:1rem; font-weight:700; margin-bottom:16px; }
  .field { margin-bottom:12px; }
  .field label { display:block; font-size:.78rem; color:#555; margin-bottom:4px; font-weight:500; }
  .field input, .field select { width:100%; padding:7px 10px; border:1px solid #ddd;
                                border-radius:6px; font-size:.85rem; }
  .field .hint { font-size:.75rem; color:#888; margin-top:3px; }
  .row2 { display:flex; gap:10px; }
  .row2 .field { flex:1; }
  .radio-group { display:flex; gap:16px; margin-top:4px; }
  .radio-group label { display:flex; align-items:center; gap:5px; font-size:.85rem;
                       font-weight:400; color:#333; cursor:pointer; }
  .modal-footer { display:flex; justify-content:flex-end; gap:8px; margin-top:18px; }
  .result-box { margin-top:14px; padding:12px; border-radius:6px; font-size:.85rem;
                background:#e6f4ea; color:#1e7e34; word-break:break-all; display:none; }
  .result-box.err { background:#fde8e8; color:#c00; }
  .tabs { display: flex; gap: 6px; flex-wrap: wrap; }
  .tab { padding: 6px 14px; border-radius: 20px; border: 1px solid #ddd;
         background: #fff; cursor: pointer; font-size: .85rem; transition: all .15s; }
  .tab.active { background: #c8102e; color: #fff; border-color: #c8102e; }
  .right { margin-left: auto; display: flex; gap: 8px; align-items: center; }
  select, input[type=text] { padding: 6px 10px; border: 1px solid #ddd; border-radius: 6px;
           font-size: .85rem; background: #fff; }
  input[type=text] { width: 180px; }
  .btn { padding: 6px 14px; background: #222; color: #fff; border: none;
         border-radius: 6px; cursor: pointer; font-size: .85rem; }
  .btn:hover { background: #444; }
  .btn-red { background: #c8102e; }
  .btn-red:hover { background: #a00d25; }
  main { padding: 20px 24px; max-width: 1100px; margin: 0 auto; }
  .count { font-size: .85rem; color: #666; margin-bottom: 12px; }
  table { width: 100%; border-collapse: collapse; background: #fff;
          border-radius: 8px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,.08); }
  th { background: #fafafa; padding: 10px 14px; text-align: left;
       font-size: .8rem; color: #666; font-weight: 600;
       border-bottom: 1px solid #e8e8e8; white-space: nowrap; }
  td { padding: 10px 14px; font-size: .85rem; border-bottom: 1px solid #f0f0f0;
       vertical-align: middle; }
  tr:last-child td { border-bottom: none; }
  tr:hover td { background: #fafafa; }
  .num { font-family: monospace; font-weight: 600; font-size: .82rem; }
  .badge { display: inline-block; padding: 3px 9px; border-radius: 12px;
           font-size: .75rem; font-weight: 500; white-space: nowrap; }
  .s-received { background: #e6f4ea; color: #1e7e34; }
  .s-transit  { background: #fff3cd; color: #856404; }
  .s-arrived  { background: #cfe2ff; color: #0a58ca; }
  .s-other    { background: #f0f0f0; color: #555; }
  .info  { text-align: center; padding: 60px; color: #888; font-size: .9rem; line-height: 1.6; }
  .error { text-align: center; padding: 60px; color: #c8102e; }
  @media (max-width: 640px) {
    main { padding: 12px; }
    th:nth-child(4), td:nth-child(4) { display: none; }
    input[type=text] { width: 130px; }
  }
</style>
</head>
<body>
<header>
  <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
    <circle cx="14" cy="14" r="14" fill="white" opacity=".2"/>
    <path d="M8 10h12v11H8zM11 10V8a3 3 0 016 0v2" stroke="white" stroke-width="1.8"
          fill="none" stroke-linecap="round"/>
  </svg>
  <div><h1>Мої посилки</h1><div class="sub">Нова Пошта</div></div>
</header>

<div class="controls">
  <div class="tabs">
    <button class="tab active" data-filter="all">Всі</button>
    <button class="tab" data-filter="transit">В дорозі</button>
    <button class="tab" data-filter="arrived">На відділенні</button>
    <button class="tab" data-filter="received">Отримані</button>
  </div>
  <div class="right">
    <button class="btn btn-red" onclick="openModal()">📦 Нова накладна</button>
    <input type="text" id="trackInput" placeholder="Номер посилки…" maxlength="20">
    <button class="btn btn-red" onclick="trackOne()">Відстежити</button>
    <select id="months" title="Період для відправлених">
      <option value="1">1 місяць</option>
      <option value="3" selected>3 місяці</option>
      <option value="6">6 місяців</option>
      <option value="12">Рік</option>
    </select>
    <button class="btn" onclick="loadSent()">↻</button>
  </div>
</div>

<main>
  <div class="count" id="count"></div>
  <div id="content"><div class="info">Завантаження…</div></div>
</main>

<!-- quick-create modal -->
<div class="overlay">
  <div class="modal">
    <h2>📦 Нова накладна → Харків</h2>

    <div class="row2">
      <div class="field">
        <label>Відправник</label>
        <select id="senderSel"></select>
      </div>
      <div class="field" style="max-width:130px">
        <label>Телефон відправника</label>
        <input type="tel" id="senderPhone" placeholder="0987712545">
      </div>
    </div>
    <div class="row2">
      <div class="field" style="max-width:140px">
        <label>Місто відправлення</label>
        <select id="senderCity" onchange="searchWarehouses()">
          <option value="lviv">Львів</option>
          <option value="kyiv">Київ</option>
        </select>
      </div>
      <div class="field">
        <label>Пошук відділення</label>
        <input type="text" id="warehouseSearch" placeholder="№ або назва…" oninput="searchWarehouses()">
      </div>
    </div>
    <div class="field">
      <label>Відділення (куди принесете посилку)</label>
      <select id="senderAddrSel"><option value="">— введіть місто для пошуку —</option></select>
    </div>

    <hr style="margin:14px 0;border:none;border-top:1px solid #eee">

    <div class="field">
      <label>Отримувач — Прізвище Ім'я По-батькові</label>
      <input type="text" id="recName" placeholder="Іваненко Іван Іванович">
    </div>
    <div class="field">
      <label>Телефон отримувача</label>
      <input type="tel" id="recPhone" placeholder="+380XXXXXXXXX">
    </div>

    <hr style="margin:14px 0;border:none;border-top:1px solid #eee">

    <div class="field"><label>Місто доставки</label>
      <input type="text" value="Харків" disabled style="background:#f5f5f5;color:#888">
    </div>
    <div class="row2">
      <div class="field"><label>Вулиця</label>
        <input type="text" id="street" value="Ощепкова"></div>
      <div class="field"><label>Будинок</label>
        <input type="text" id="building" value="4/1"></div>
      <div class="field" style="max-width:80px"><label>Кв.</label>
        <input type="text" id="flat" value="28"></div>
    </div>

    <hr style="margin:14px 0;border:none;border-top:1px solid #eee">

    <div class="row2">
      <div class="field"><label>Опис</label>
        <input type="text" id="description" value="Документи"></div>
      <div class="field" style="max-width:110px"><label>Цінність, грн</label>
        <input type="number" id="cost" value="1000" min="1"></div>
      <div class="field" style="max-width:90px"><label>Вага, кг</label>
        <input type="number" id="weight" value="0.1" min="0.1" step="0.1"></div>
    </div>
    <div class="field">
      <label>Хто платить за доставку</label>
      <div class="radio-group">
        <label><input type="radio" name="payer" value="Recipient" checked> Отримувач</label>
        <label><input type="radio" name="payer" value="Sender"> Відправник</label>
      </div>
    </div>

    <div id="modal-result" class="result-box"></div>

    <div class="modal-footer">
      <button class="btn" onclick="closeModal()">Скасувати</button>
      <button class="btn btn-red" id="createBtn" onclick="submitCreate()">Створити накладну</button>
    </div>
  </div>
</div>

<script>
window.onerror = function(msg, src, line) {
  document.body.insertAdjacentHTML('afterbegin',
    '<div style="background:#c00;color:#fff;padding:12px 16px;font-size:13px;' +
    'position:fixed;top:0;left:0;right:0;z-index:9999;font-family:monospace">' +
    'JS ERROR line ' + line + ': ' + msg + '</div>');
};

let all = [], filter = 'all';

const statusClass = s => {
  const l = s.toLowerCase();
  if (l.includes('отримано') || l.includes('вручено')) return 's-received';
  if (l.includes('прибув') || l.includes('відділенні'))   return 's-arrived';
  if (l.includes('дорозі') || l.includes('дорог') || l.includes('вантажній') || l.includes('транзит')) return 's-transit';
  return 's-other';
};

const filtered = () => {
  if (filter === 'all')      return all;
  if (filter === 'received') return all.filter(d => statusClass(d.status) === 's-received');
  if (filter === 'arrived')  return all.filter(d => statusClass(d.status) === 's-arrived');
  if (filter === 'transit')  return all.filter(d => statusClass(d.status) === 's-transit');
  return all;
};

function render() {
  const data = filtered();
  document.getElementById('count').textContent =
    `${data.length} посилок${all.length !== data.length ? ' з ' + all.length : ''}`;
  const el = document.getElementById('content');
  if (!data.length) {
    el.innerHTML = `<div class="info">Посилок не знайдено.<br>
      Спробуй відстежити конкретний номер або змінити період.</div>`;
    return;
  }
  const isSent = data[0] && data[0].sender !== undefined;
  el.innerHTML = isSent ? renderTrack(data) : renderSent(data);
}

function renderSent(data) {
  return `<table><thead><tr>
    <th>Номер</th><th>Отримувач</th><th>Статус</th><th>Адреса</th><th>Дата</th>
  </tr></thead><tbody>
  ${data.map(d => `<tr>
    <td><span class="num">${d.number}</span></td>
    <td>${d.person}</td>
    <td><span class="badge ${statusClass(d.status)}">${d.status}</span></td>
    <td>${d.address}</td>
    <td>${d.date}</td>
  </tr>`).join('')}
  </tbody></table>`;
}

function renderTrack(data) {
  return `<table><thead><tr>
    <th>Номер</th><th>Що</th><th>Маршрут</th><th>Статус</th><th>Отримувач</th><th>Дата</th>
  </tr></thead><tbody>
  ${data.map(d => `<tr>
    <td><span class="num">${d.number}</span></td>
    <td>${d.description || '—'}${d.weight ? `<br><span style="color:#888;font-size:.8em">${d.weight} кг · ${d.cost} грн</span>` : ''}</td>
    <td style="white-space:nowrap">${d.from_city} → ${d.to_city}</td>
    <td><span class="badge ${statusClass(d.status)}">${d.status}</span></td>
    <td>${d.recipient || d.sender || '—'}<br><span style="color:#888;font-size:.8em">${d.address || ''}</span></td>
    <td>${d.date_done || d.date_sent || '—'}</td>
  </tr>`).join('')}
  </tbody></table>`;
}

async function loadSent() {
  document.getElementById('content').innerHTML = '<div class="info">Завантаження…</div>';
  document.getElementById('count').textContent = '';
  const months = document.getElementById('months').value;
  try {
    const r = await fetch(`/parcels/api/sent?months=${months}`);
    if (!r.ok) throw new Error(r.statusText);
    all = await r.json();
    render();
  } catch(e) {
    document.getElementById('content').innerHTML = `<div class="error">${e.message}</div>`;
  }
}

async function trackOne() {
  const num = document.getElementById('trackInput').value.trim();
  if (!num) return;
  document.getElementById('content').innerHTML = '<div class="info">Відстеження…</div>';
  document.getElementById('count').textContent = '';
  try {
    const r = await fetch(`/parcels/api/track?n=${encodeURIComponent(num)}`);
    if (!r.ok) throw new Error(r.statusText);
    const data = await r.json();
    all = data;
    document.querySelectorAll('.tab').forEach(b => b.classList.remove('active'));
    document.querySelector('[data-filter=all]').classList.add('active');
    filter = 'all';
    render();
  } catch(e) {
    document.getElementById('content').innerHTML = `<div class="error">${e.message}</div>`;
  }
}

document.getElementById('trackInput').addEventListener('keydown', e => {
  if (e.key === 'Enter') trackOne();
});

// ── Quick-create modal ────────────────────────────────────────────────────────

let senderData = null;

async function openModal() {
  document.getElementById('modal-result').className = 'result-box';
  document.getElementById('modal-result').textContent = '';
  document.getElementById('createBtn').disabled = false;
  document.getElementById('createBtn').textContent = 'Створити накладну';
  document.querySelector('.overlay').classList.add('open');
  if (!senderData) await loadSenders();
}
function closeModal() {
  document.querySelector('.overlay').classList.remove('open');
}
document.querySelector('.overlay').addEventListener('click', e => {
  if (e.target === e.currentTarget) closeModal();
});

async function loadSenders() {
  const sel = document.getElementById('senderSel');
  sel.innerHTML = '<option>Завантаження…</option>';
  try {
    const r = await fetch('/parcels/api/senders');
    if (!r.ok) throw new Error(r.statusText);
    senderData = await r.json();
    if (senderData.error) throw new Error(senderData.error);
    sel.innerHTML = senderData.map((s,i) =>
      `<option value="${i}">${s.contacts[0] ? s.contacts[0].name : s.name}</option>`).join('');
  } catch(e) {
    sel.innerHTML = `<option>Помилка: ${e.message}</option>`;
  }
  searchWarehouses();
}

let warehouseTimer = null;
function searchWarehouses() {
  clearTimeout(warehouseTimer);
  warehouseTimer = setTimeout(_doWarehouseSearch, 400);
}
async function _doWarehouseSearch() {
  const cityKey = document.getElementById('senderCity').value;
  const search  = document.getElementById('warehouseSearch').value.trim();
  const sel     = document.getElementById('senderAddrSel');
  sel.innerHTML = '<option>Пошук…</option>';
  try {
    const r = await fetch(`/parcels/api/warehouses?city=${cityKey}&search=${encodeURIComponent(search)}`);
    const data = await r.json();
    if (data.error) throw new Error(data.error);
    sel.innerHTML = data.map(w => `<option value="${w.ref}">${w.name}</option>`).join('');
    if (!sel.options.length) sel.innerHTML = '<option value="">— не знайдено —</option>';
  } catch(e) {
    sel.innerHTML = `<option value="">Помилка: ${e.message}</option>`;
  }
}

async function submitCreate() {
  const idx = parseInt(document.getElementById('senderSel').value) || 0;
  const s = senderData && senderData[idx];
  if (!s) return;

  const contact  = s.contacts[0] || {};
  const addrRef  = document.getElementById('senderAddrSel').value;
  const phone    = document.getElementById('senderPhone').value.trim().replace(/\D/g,'');

  if (!addrRef) { showResult('Оберіть відділення відправлення', true); return; }
  if (!phone)   { showResult('Вкажіть телефон відправника', true); return; }

  const body = {
    sender_ref:         s.ref,
    city_sender_key:    document.getElementById('senderCity').value,
    contact_sender_ref: contact.ref || '',
    senders_phone:      phone,
    sender_address_ref: addrRef,
    recipient_name:  document.getElementById('recName').value.trim(),
    recipient_phone: document.getElementById('recPhone').value.trim().replace(/\D/g,''),
    street:   document.getElementById('street').value.trim(),
    building: document.getElementById('building').value.trim(),
    flat:     document.getElementById('flat').value.trim(),
    description: document.getElementById('description').value.trim(),
    cost:     document.getElementById('cost').value.trim(),
    weight:   document.getElementById('weight').value.trim(),
    payer_type: document.querySelector('input[name=payer]:checked').value,
  };

  if (!body.recipient_name || !body.recipient_phone) {
    showResult('Вкажіть імʼя та телефон отримувача', true); return;
  }

  const btn = document.getElementById('createBtn');
  btn.disabled = true; btn.textContent = 'Створення…';
  try {
    const r = await fetch('/parcels/api/create_waybill', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(body),
    });
    const data = await r.json();
    if (!r.ok || data.error) throw new Error(data.error || r.statusText);
    const num = data.IntDocNumber || data.Ref || JSON.stringify(data);
    showResult(`✅ Накладна створена: ${num}`, false);
    btn.textContent = 'Готово';
  } catch(e) {
    showResult(e.message, true);
    btn.disabled = false; btn.textContent = 'Створити накладну';
  }
}

function showResult(msg, isErr) {
  const el = document.getElementById('modal-result');
  el.textContent = msg;
  el.className = 'result-box' + (isErr ? ' err' : '');
  el.style.display = 'block';
}

document.querySelectorAll('.tab').forEach(btn => btn.addEventListener('click', () => {
  document.querySelectorAll('.tab').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  filter = btn.dataset.filter;
  render();
}));

document.getElementById('months').addEventListener('change', loadSent);
loadSent();
</script>
</body>
</html>"""


@app.route("/")
def index():
    return render_template_string(HTML)


def _waybill_numbers(months: int) -> list[str]:
    """Get all sent waybill numbers for last N months (3-month chunks)."""
    date_to   = datetime.now()
    date_from = date_to - timedelta(days=months * 31)
    fmt       = "%d.%m.%Y"
    numbers   = []
    chunk_end = date_to
    while chunk_end > date_from:
        chunk_start = max(chunk_end - timedelta(days=89), date_from)
        try:
            docs = np.list_waybills(chunk_start.strftime(fmt), chunk_end.strftime(fmt))
            numbers.extend(d["IntDocNumber"] for d in docs if d.get("IntDocNumber"))
        except Exception as e:
            log.warning("list_waybills chunk failed: %s", e)
        chunk_end = chunk_start - timedelta(days=1)
    return numbers


def _format_tracked(items: list[dict]) -> list[dict]:
    out = []
    for d in items:
        date_raw = (d.get("ActualDeliveryDate") or
                    d.get("ScheduledDeliveryDate") or
                    d.get("DateCreated") or "")
        out.append({
            "number":      d.get("Number", ""),
            "status":      d.get("Status", ""),
            "description": d.get("CargoDescriptionString", ""),
            "weight":      d.get("CheckWeight") or d.get("DocumentWeight", ""),
            "cost":        d.get("CostOnSite") or d.get("DocumentCost", ""),
            "from_city":   d.get("CitySender", ""),
            "to_city":     d.get("CityRecipient", ""),
            "sender":      d.get("SenderFullNameEW", ""),
            "recipient":   d.get("RecipientFullNameEW", ""),
            "address":     d.get("WarehouseRecipient") or d.get("RecipientAddress", ""),
            "date_sent":   (d.get("DateCreated") or "")[:10],
            "date_done":   date_raw[:10] if date_raw else "",
        })
    return out


@app.route("/api/sent")
def api_sent():
    months  = max(1, min(int(request.args.get("months", 3)), 12))
    numbers = _waybill_numbers(months)
    if not numbers:
        return jsonify([])
    try:
        tracked = np.track(*numbers)
    except Exception as e:
        log.warning("track failed: %s", e)
        return jsonify([])
    out = _format_tracked(tracked)
    out.sort(key=lambda x: x["date_sent"], reverse=True)
    return jsonify(out)


@app.route("/api/track")
def api_track():
    n = request.args.get("n", "").strip()
    if not n:
        return jsonify([])
    try:
        items = np.track(n)
    except Exception as e:
        return jsonify({"error": str(e)}), 400

    out = []
    for d in items:
        date_raw = d.get("ActualDeliveryDate") or d.get("ScheduledDeliveryDate") or d.get("DateCreated") or ""
        out.append({
            "number":      d.get("Number", n),
            "status":      d.get("Status", ""),
            "description": d.get("CargoDescriptionString", ""),
            "weight":      d.get("CheckWeight") or d.get("DocumentWeight", ""),
            "cost":        d.get("DocumentCost", ""),
            "from_city":   d.get("CitySender", ""),
            "to_city":     d.get("CityRecipient", ""),
            "sender":      d.get("SenderFullNameEW", ""),
            "recipient":   d.get("RecipientFullNameEW", ""),
            "address":     d.get("WarehouseRecipient") or d.get("RecipientAddress", ""),
            "date_sent":   (d.get("DateCreated") or "")[:10],
            "date_done":   date_raw[:10] if date_raw else "",
            "service":     d.get("ServiceType", ""),
        })
    return jsonify(out)


@app.route("/api/senders")
def api_senders():
    try:
        senders = np.get_counterparties("Sender")
        result = []
        for s in senders:
            ref      = s.get("Ref", "")
            city_ref = s.get("CityRef", "")
            try:
                contacts  = np.get_contact_persons(ref)
                addresses = np.get_counterparty_addresses(ref, city_ref) if city_ref else []
            except Exception:
                contacts, addresses = [], []
            result.append({
                "ref":      ref,
                "name":     s.get("FullName") or s.get("Description", ""),
                "city_ref": city_ref,
                "city":     s.get("CityDescription", ""),
                "phone":    s.get("Phone", ""),
                "contacts": [{"ref": c.get("Ref",""), "name": c.get("Description","")} for c in contacts],
                "addresses": [{"ref": a.get("Ref",""), "name": a.get("Description","")} for a in addresses],
            })
        return jsonify(result)
    except Exception as e:
        log.warning("api_senders: %s", e)
        return jsonify({"error": str(e)}), 400


CITY_REFS = {
    "lviv":    "db5c88f5-391c-11dd-90d9-001a92567626",
    "kyiv":    "8d5a980d-391c-11dd-90d9-001a92567626",
    "kharkiv": "db5c88e0-391c-11dd-90d9-001a92567626",
}


@app.route("/api/warehouses")
def api_warehouses():
    city_key = request.args.get("city", "lviv").strip().lower()
    search   = request.args.get("search", "").strip()
    city_ref = CITY_REFS.get(city_key)
    if not city_ref:
        return jsonify({"error": f"Unknown city key: {city_key}"}), 400
    try:
        warehouses = np.find_warehouse(city_ref, search)
        return jsonify([{"ref": w.get("Ref",""), "name": w.get("Description","")} for w in warehouses])
    except Exception as e:
        return jsonify({"error": str(e)}), 400


def _resolve_city(name: str) -> str:
    """Return Ref for city by name; raises RuntimeError if not found."""
    cities = np.find_city(name)
    city   = next((c for c in cities if c.get("Description", "").startswith(name)), None)
    if not city:
        raise RuntimeError(f"Місто «{name}» не знайдено")
    return city["Ref"]


@app.route("/api/create_waybill", methods=["POST"])
def api_create_waybill():
    data = request.json or {}

    city_recipient_ref = CITY_REFS["kharkiv"]
    sender_city_key    = data.get("city_sender_key", "lviv")
    city_sender_ref    = CITY_REFS.get(sender_city_key, CITY_REFS["lviv"])

    # Resolve street
    street_name = data.get("street", "Ощепкова")
    try:
        streets   = np.find_street(city_recipient_ref, street_name)
        street    = streets[0] if streets else None
        if not street:
            return jsonify({"error": f"Вулиця «{street_name}» не знайдена у Харкові"}), 400
        street_ref = street["Ref"]
    except Exception as e:
        return jsonify({"error": f"Пошук вулиці: {e}"}), 400

    # Create recipient counterparty
    recipient_name  = data.get("recipient_name", "").strip()
    recipient_phone = data.get("recipient_phone", "").strip()
    if not recipient_name or not recipient_phone:
        return jsonify({"error": "Вкажіть ім'я та телефон отримувача"}), 400
    parts = recipient_name.split()
    last, first, mid = (parts + ["", ""])[:3]
    try:
        rec         = np.create_counterparty(first, last, mid, recipient_phone)
        rec_ref     = rec.get("Ref", "")
        contact_persons = rec.get("ContactPerson", {}).get("data", [])
        contact_ref = contact_persons[0].get("Ref", "") if contact_persons else ""
    except Exception as e:
        return jsonify({"error": f"Створення отримувача: {e}"}), 400

    # Create door address for recipient
    try:
        addr     = np.save_recipient_address(
            rec_ref, city_recipient_ref, street_ref,
            data.get("building", "4/1"), data.get("flat", "28"))
        addr_ref = addr.get("Ref", "")
    except Exception as e:
        return jsonify({"error": f"Створення адреси: {e}"}), 400

    payload = {
        "PayerType":     data.get("payer_type", "Recipient"),
        "PaymentMethod": "Cash",
        "DateTime":      datetime.now().strftime("%d.%m.%Y"),
        "CargoType":     "Parcel",
        "Weight":        str(data.get("weight", "0.1")),
        "ServiceType":   "WarehouseDoors",
        "SeatsAmount":   "1",
        "Description":   data.get("description", "Документи"),
        "Cost":          str(data.get("cost", "1000")),
        "CitySender":         city_sender_ref,
        "Sender":             data.get("sender_ref", ""),
        "SenderAddress":      data.get("sender_address_ref", ""),
        "ContactSender":      data.get("contact_sender_ref", ""),
        "SendersPhone":       data.get("senders_phone", ""),
        "CityRecipient":      city_recipient_ref,
        "Recipient":          rec_ref,
        "RecipientAddress":   addr_ref,
        "ContactRecipient":   contact_ref,
        "RecipientsPhone":    recipient_phone,
    }
    try:
        result = np.create_waybill(payload)
        return jsonify(result)
    except Exception as e:
        log.warning("create_waybill: %s", e)
        return jsonify({"error": str(e)}), 400


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    port = int(os.environ.get("PORT", 8601))
    app.run(host="127.0.0.1", port=port)
