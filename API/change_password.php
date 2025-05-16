<?php
require 'config.php';
$input = json_decode(file_get_contents('php://input'), true);

// Validaciones básicas
if (empty($input['email']) || empty($input['new_password'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Email y nueva contraseña obligatorios']);
    exit;
}
if (!filter_var($input['email'], FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(['error' => 'Email inválido']);
    exit;
}

// Comprobamos que el email exista
$stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
$stmt->execute([$input['email']]);
$user = $stmt->fetch();

if (!$user) {
    http_response_code(404);
    echo json_encode(['error' => 'Usuario no encontrado']);
    exit;
}

// Hasheamos la nueva contraseña y actualizamos
$newHash = password_hash($input['new_password'], PASSWORD_BCRYPT);
$upd = $pdo->prepare("UPDATE users SET password_hash = ? WHERE id = ?");
$upd->execute([$newHash, $user['id']]);

echo json_encode(['message' => 'Contraseña modificada correctamente']);
http_response_code(200);