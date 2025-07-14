
Up-to-date overview of modern software development tools and techniques

Banking domain

bcm industry

https://career.luxoft.com/jobs/senior-java-developer-15269



🔹 Структура співбесіди (типова)


✅ 1. Tell me about yourself
My name is Dmytro. I’ve been working as a Java developer for over 10 years.
Most of that time I spent at the same company, where I went through different roles —
from working on a monolithic agent evaluation system to leading development of
microservices for importing call recordings.

I collaborated closely with the Product Owner and was part of the transition
from on-prem deployments to cloud infrastructure in AWS using Docker and Kubernetes.

I’m a responsible and well-organized person. I enjoy solving backend problems,
working with clean code, and being part of a focused and friendly team.

✅ 2. Why are you looking for a new job?
The main reason is the limitations of being an external developer — no paid vacations,
no sick days, and restricted access to tools, documentation, and repos compared to developer on-site.
Had to ask some actions to do for me.

Also, I’ve been with the same company for a long time and feel it’s time
to refresh my perspective, explore new technologies, and grow further
in a different environment.

✅ 3. Why Luxoft?
Luxoft is a stable company with strong engineering practices and interesting projects.
I see it as a place where I can apply my experience and keep growing.

✅ What were the key projects you worked on?
I was involved in several backend-focused projects during my time at the company.
One of the main ones was a monolithic application for agent evaluation, where I
eventually became a team lead.

Later, I moved to a distributed system built around importing call recordings
from external recording servers. This became my main area for several years.
I worked on a group of microservices responsible for importing, transforming,
and storing call data.

I collaborated closely with the Product Owner and took part in technical planning
and decision-making.

✅ What kind of architecture did you use?
We started with a legacy monolithic system deployed on-prem, then gradually moved
to a cloud-native setup.

The new services were designed as microservices running in AWS, orchestrated
with Kubernetes and Docker. We used a REST-based architecture and integrated
multiple third-party systems.


✅ What technologies did you use?
Mainly Java and Spring Boot for backend development, along with RabbitMQ for async
communication in some modules.

We also used PostgreSQL.

For security, we integrated with Keycloak. Everything was containerized with Docker
and deployed in Kubernetes on AWS. CI/CD was handled with Bamboo pipelines.

✅ What was your role? Did you influence architecture?
In the monolithic project, I eventually became a team lead and was involved
in shaping architecture and managing the team.

In the microservices part, I started as a developer and later became a lead there too.

I took part in defining how services should be split, what APIs to expose,
and how to handle cross-cutting concerns like authentication and session management.

I was also regularly involved in code reviews, mentoring, and helping newer team members.

✅ Tell me about a complex technical decision you made.
One of the challenges I solved was related to authentication and session handling.

We had several embedded React frontends running inside our monolithic app.
At first, they managed their own authentication flows and communicated
directly with Keycloak.

That led to session conflicts and inconsistent behavior across services.

I proposed a unified approach — the monolith would take full ownership
of authentication and session renewal.

Frontend apps only sent session prolongation requests to the backend,
which in turn updated its own session and handled refresh tokens in memory.

This simplified the flow, avoided race conditions, and made the system
more reliable and secure.

✅ Were there cases where you had to rewrite part of the project? Why?
Yes, there were several such cases.

For example, when external systems changed their APIs, we had to refactor
multiple services to support the updated structure.

In some cases, it wasn’t just about adapting — we had to redesign part of
the service logic to better match the new data model.

We also rewrote certain components to improve performance and maintainability
as the system evolved.


✅ 3. Процеси та співпраця (10–15 хв)
📅 Agile / Scrum
I’ve worked with both Scrum and Kanban.

Scrum was great for larger teams and projects with well-defined features and milestones.
We used regular sprints, retrospectives, and velocity tracking to improve predictability.

In other cases, especially for support or integration work, Kanban worked better.
It allowed for continuous delivery and quick reaction to changing priorities,
which was perfect for smaller tasks or bug fixing.

I’m comfortable using either depending on the nature of the team and the project.

👥 Командна робота
Скільки було людей у команді?

Як вирішували конфлікти?

Як ти допомагав менторити молодших?

Чи проводив code review?

✅ 4. Робота з клієнтом (10 хв)
Чи був прямий контакт з клієнтом?

Як ти пояснюєш складні технічні речі бізнес-замовнику?

Що робити, коли клієнт вимагає те, що технічно недоцільно?

Як ти дієш, коли клієнт змінює вимоги в останній момент?

✅ 5. Ситуаційні кейси (10 хв)
Це важливо, бо Luxoft часто працює з фінансовими клієнтами, де відповідальність і стресостійкість критичні.

Ситуація	Твої дії
🔥 Критичний баг в продакшені	"Я б перевірив логування, зібрав команду, пріоритезував фікс, повідомив PM"
📦 Новий реліз через 2 дні, а код нестабільний	"Я б запропонував відкласти реліз, щоб не наражати клієнта на ризики"
⚖️ Конфлікт з іншим сеньйором	"Обговорюю технічну суть, залучаю тімліда, шукаю компроміс"
📈 Потрібно оптимізувати запит, що вантажить базу	"Я аналізую індекси, explain plan, альтернативні структури"

🔹 Що важливо підкреслити?
Самостійність — вміння приймати рішення без постійного контролю.

Зрілість — ти не просто "кодиш", а розумієш бізнес-цілі.

Відповідальність — розумієш наслідки своїх рішень.

Комунікабельність — можеш говорити з бізнесом, а не лише з технічними колегами.

Гнучкість — не боїшся змін, працюєш у стресі.

❗ Поради:
Підготуй 2–3 сильні історії: технічне рішення, конфлікт у команді, критичний реліз.

Покажи, що ти можеш вести інших, брати ініціативу.

Дай зрозуміти, що можеш вільно говорити англійською (у Luxoft це часто критично).

Не бійся питати менеджера про проект, команду, плани — це показує твою залученість.