<?php
require 'config.php';
$input = json_decode(file_get_contents('php://input'), true);

if (empty($input['email']) || empty($input['password'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Email y contraseña obligatorios']);
    exit;
}

if (!filter_var($input['email'], FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(['error' => 'Email inválido']);
    exit;
}

$passwordHash = password_hash($input['password'], PASSWORD_BCRYPT);

$stmt = $pdo->prepare("INSERT INTO users (email, password_hash) VALUES (?, ?)");
try {
    $stmt->execute([$input['email'], $passwordHash]);
    http_response_code(201);
    echo json_encode(['message' => 'Usuario registrado']);
} catch (PDOException $e) {
    http_response_code(409);
    echo json_encode(['error' => 'Email ya en uso']);
}
