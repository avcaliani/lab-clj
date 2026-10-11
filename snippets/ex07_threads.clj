;;; Exercise 07
;;;     Threads (background consumer loop with explicit lifecycle)
;;;     Prep for issue #26. No Kafka, no AWS: a queue plays the topic.
;;
;; Why this exercise?
;;      Issue #26 adds a Kafka consumer to dispatch-api. Unlike HTTP handlers (Jetty
;;      owns those threads), a consumer is a long-running poll loop that WE must
;;      start, stop and clean up. Getting it wrong means lost messages, dead
;;      clients or a process that never exits. Practicing it here, without Kafka
;;      or AWS, keeps the focus on the thread lifecycle.
(ns ex07-threads
  (:import (java.util.concurrent LinkedBlockingQueue TimeUnit)))

;; Python analogue: threading.Thread + queue.Queue + threading.Event.
;; The queue is the "topic", the loop is the "consumer", the atom is the "stop flag".
;; A handle is just a map describing what is running, same idea as the tools map in #25.
;;
;; Rules of the game:
;;   - Only the loop thread reads from the queue (like KafkaConsumer, not thread-safe).
;;   - The main thread only starts, stops and produces messages.
;;   - A "message" is a map like {:id "1" :severity "low"}.
(def topic (LinkedBlockingQueue.))


;; ─── EXERCISES ───────────────────────────────────────────────────────────────
;; 1. Run something in the background.
;;    Start a future that sleeps 1s and returns :done. Print right after starting it.
;;    Question: what prints first? Deref the future to get the value.
;;    Hint: future, Thread/sleep, @


;; 2. Poll with a timeout.
;;    Write `poll-message` that takes the queue and a timeout in ms and returns
;;    the next message or nil if nothing arrived in time.
;;    Hint: (.poll queue timeout TimeUnit/MILLISECONDS)
;;    Python equivalent: queue.get(timeout=...) but returning None instead of raising.


;; 3. Start the consumer.
;;    Write `start-consumer!` that takes the queue and a handler fn, and returns a handle map.
;;    The loop polls (ex. 2) and calls the handler for every message, until told to stop.
;;    Handle suggestion: {:running? <atom> :worker <future>}
;;    Hint: loop/recur inside the future, check @running? on every iteration.
;;    Put 3 messages in the queue and see the handler print them.


;; 4. Stop the consumer.
;;    Write `stop-consumer!` that takes the handle, flips the flag and WAITS for the loop to finish.
;;    Question: why wait? What could go wrong if the caller moves on right away?
;;    Hint: deref with a timeout, (deref f ms timeout-val).


;; 5. Shutdown order (this is the one that matters in #26).
;;    Create a fake "db" resource with an atom (:open or :closed).
;;    The handler writes to it. If it is closed, the handler must throw.
;;    Wire start and stop so the db is closed only AFTER the consumer is done.
;;    Then break the order on purpose and watch what happens.


;; 6. Exceptions in a future.
;;    Make the handler throw on a message with :severity "boom".
;;    Question: what do you see in the terminal? Is the consumer still alive?
;;    Then fix the loop: catch, log, and keep consuming the next message.
;;    Hint: try/catch inside the loop, not around it.


;; 7. Challenge: stop in the middle of a message.
;;    Make the handler slow (Thread/sleep 2000) and call stop-consumer! while it is working.
;;    Expected: the message finishes (handler completes), then the loop exits.
;;    Question: what is the worst case wait time for stop? How do you bound it?
;;    Bonus: Kafka uses `consumer.wakeup()` to cut the poll short. What would the
;;    equivalent be here?
