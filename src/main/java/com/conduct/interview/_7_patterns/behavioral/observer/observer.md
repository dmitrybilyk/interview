Observer pattern handles the situations when changes
in some object other objects should be aware of.

Interface Observable(methods subscribe, unsubscribe, notify),
Observer(method update)

Implementor of the Observable contains the list of
observer objects to be subscribed and notified when
event occurs.