// WebSocket notifications
class NotificationService {
    constructor() {
        this.stompClient = null;
        this.callbacks = new Map();
        this.userId = null;
    }

    connect(userId) {
        this.userId = userId;
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, (frame) => {
            console.log('WebSocket connected:', frame);

            // Subscribe to user queue
            this.stompClient.subscribe(`/user/${userId}/queue/messages`, (message) => {
                const data = JSON.parse(message.body);
                this.handleNotification(data);
            });

            // Subscribe to typing events
            this.stompClient.subscribe('/topic/typing', (message) => {
                const data = JSON.parse(message.body);
                if (data.receiverId === this.userId) {
                    this.handleTyping(data);
                }
            });
        });
    }

    on(event, callback) {
        if (!this.callbacks.has(event)) {
            this.callbacks.set(event, []);
        }
        this.callbacks.get(event).push(callback);
    }

    handleNotification(data) {
        const callbacks = this.callbacks.get('notification') || [];
        callbacks.forEach(cb => cb(data));

        // Show browser notification
        if (Notification.permission === 'granted') {
            new Notification(data.title, {
                body: data.body,
                icon: '/favicon.ico'
            });
        }
    }

    handleTyping(data) {
        const callbacks = this.callbacks.get('typing') || [];
        callbacks.forEach(cb => cb(data));
    }

    sendTyping(conversationId, receiverId) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send('/app/typing', {}, JSON.stringify({
                senderId: this.userId,
                receiverId: receiverId,
                conversationId: conversationId
            }));
        }
    }

    sendMessage(message) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send('/app/chat.send', {}, JSON.stringify(message));
        }
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
    }
}

// Initialize notification service
const notificationService = new NotificationService();

// Request notification permission
if (Notification.permission === 'default') {
    Notification.requestPermission();
}

window.notificationService = notificationService;