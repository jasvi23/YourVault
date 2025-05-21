<?php
header('Content-Type: application/json');
include 'config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data['email']) && !empty($data['password'])) {
    $email = $data['email'];
    $newPassword = $data['password'];

    // Hashear la nueva contraseña
    $newPasswordHash = password_hash($newPassword, PASSWORD_DEFAULT);

    try {
        // Comprobamos si el usuario existe
        $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if ($user) {
            // Actualizamos la contraseña
            $updateStmt = $pdo->prepare("UPDATE users SET current_password_hash = ? WHERE email = ?");
            $updateStmt->execute([$newPasswordHash, $email]);

            echo json_encode(["success" => "Contraseña actualizada correctamente"]);
        } else {
            echo json_encode(["error" => "Usuario no encontrado"]);
        }

    } catch (PDOException $e) {
        echo json_encode(["error" => "Error al actualizar la contraseña"]);
    }

} else {
    echo json_encode(["error" => "Email y nueva contraseña son obligatorios"]);
}
