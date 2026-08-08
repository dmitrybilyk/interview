Threads don't sit and wait - they react when something happens (an event listener fires, a
message arrives, a UI click). Between events, no thread is tied up at all.

Examples: event listeners, message consumers (Kafka/Rabbit), UI events, reactive streams
(see `sync_async/AsyncWithReactiveProject`).
